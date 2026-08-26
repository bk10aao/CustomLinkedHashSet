#!/usr/bin/env python3
"""
Generate performance benchmark charts as PNG files comparing
CustomLinkedSet V2 and JDK LinkedHashSet
with a transparent background, wide-format CSV loading,
custom integer-formatted y-axes starting at 0,
and pure marker circles in the legend.
"""

import matplotlib

matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.ticker as mticker
from matplotlib.lines import Line2D
import pandas as pd
import os
import sys
import numpy as np

# ──────────────────────────────────────────────────────────────────────────────
# Configuration
# ──────────────────────────────────────────────────────────────────────────────

V2_CSV_PATH  = "CustomLinkedSet_jmh_performanceV2.csv"  # Custom V2
JDK_CSV_PATH = "../LinkedHashSet_jmh_performance.csv"  # JDK LinkedHashSet
OUTPUT_DIR   = ".."  # Current directory

COLORS = {
    'purple': '#9B6EF3',   # V2
    'orange': '#FF9F43',   # JDK
    'grid':   '#252525',
}

FIGURE_SIZE = (12, 6.2)
DPI = 150

OPERATIONS = {
    'add': 'add(E)',
    'addAll': 'addAll(Collection)',
    'constructor_int_float': 'Constructor(int,float)',
    'constructor_int': 'Constructor(int)',
    'clear': 'clear()',
    'clone': 'clone()',
    'constructor_collection': 'Constructor(Collection)',
    'contains': 'contains(Object)',
    'containsAll': 'containsAll(Collection)',
    'constructor': 'Constructor()',
    'equals': 'equals(Object)',
    'hashCode': 'hashCode()',
    'isEmpty': 'isEmpty()',
    'iterator': 'iterator()',
    'remove': 'remove(Object)',
    'removeAll': 'removeAll(Collection)',
    'retainAll': 'retainAll(Collection)',
    'size': 'size()',
    'spliterator': 'spliterator()',
    'toArray': 'toArray()',
    'toArray_T': 'toArray(T[])',
    'toString': 'toString()',
}


# ──────────────────────────────────────────────────────────────────────────────
# CSV Loading
# ──────────────────────────────────────────────────────────────────────────────

def load_wide_jmh_csv(filepath):
    """Load wide-format CSV and return dict: {size: {col_name: score_value}}"""
    with open(filepath, 'r') as f:
        lines = [line.strip() for line in f if line.strip()]

    sample_line = lines[1] if len(lines) > 1 else lines[0]
    sep = ';' if ';' in sample_line else ','

    df = pd.read_csv(filepath, sep=sep)
    if len(df.columns) == 1 and len(lines) > 0:
        alt_sep = ',' if sep == ';' else ';'
        df = pd.read_csv(filepath, sep=alt_sep)

    df.columns = [c.strip() for c in df.columns]

    data = {}
    for _, row in df.iterrows():
        try:
            size = int(row['Size'])
        except (ValueError, KeyError, TypeError):
            continue
        data[size] = {}
        for col in df.columns:
            if col != 'Size':
                try:
                    data[size][col] = float(row[col])
                except (ValueError, TypeError):
                    data[size][col] = np.nan
    return data


# ──────────────────────────────────────────────────────────────────────────────
# Chart Generation
# ──────────────────────────────────────────────────────────────────────────────

def create_chart(col_name, chart_title, v2_data, jdk_data,
                 canonical_sizes, output_path):

    v2_values = [
        v2_data[s][col_name] if s in v2_data and col_name in v2_data[s] else np.nan
        for s in canonical_sizes
    ]
    jdk_values = [
        jdk_data[s][col_name] if s in jdk_data and col_name in jdk_data[s] else np.nan
        for s in canonical_sizes
    ]

    fig, ax = plt.subplots(figsize=FIGURE_SIZE, dpi=DPI)
    fig.patch.set_alpha(0)
    ax.set_facecolor('none')

    x_positions = list(range(len(canonical_sizes)))

    # Lines
    ax.plot(x_positions, v2_values,  color=COLORS['purple'], linewidth=1.5, zorder=2)
    ax.plot(x_positions, jdk_values, color=COLORS['orange'], linewidth=1.5, zorder=2)

    # Markers
    ax.scatter(x_positions, v2_values,  color=COLORS['purple'], s=35, marker='o',
               edgecolors=COLORS['purple'], linewidths=1.5, zorder=3)
    ax.scatter(x_positions, jdk_values, color=COLORS['orange'], s=35, marker='o',
               edgecolors=COLORS['orange'], linewidths=1.5, zorder=3)

    # Grid
    ax.grid(True, color=COLORS['grid'], linewidth=0.8, linestyle='-', zorder=0)
    ax.set_axisbelow(True)

    # X-axis
    ax.set_xticks(x_positions)
    ax.set_xticklabels([f'{s:,}' for s in canonical_sizes], color='white', fontsize=10)
    ax.tick_params(axis='x', colors='white', length=0, pad=8)
    ax.set_xlim(-0.5, len(canonical_sizes) - 0.5)

    # Y-axis
    ax.set_ylim(bottom=0)
    ax.ticklabel_format(style='plain', axis='y')
    ax.yaxis.set_major_formatter(mticker.FuncFormatter(lambda x, p: f'{int(x):,}'))

    if ax.yaxis.get_offset_text():
        ax.yaxis.get_offset_text().set_color('white')
        ax.yaxis.get_offset_text().set_fontsize(10)

    ax.tick_params(axis='y', colors='white', length=0, pad=8)
    for label in ax.get_yticklabels():
        label.set_color('white')
        label.set_fontsize(10)

    # Spines
    for spine in ax.spines.values():
        spine.set_visible(False)

    # Labels
    ax.set_xlabel('Size', color='white', fontsize=12, labelpad=12)
    ax.set_ylabel('Time (ns/op)', color='white', fontsize=11, labelpad=10)
    ax.set_title(chart_title, color='white', fontsize=15, fontweight='bold', pad=14)

    # Legend (V2 → JDK)
    legend_elements = [
        Line2D([0], [0], marker='o', color='none',
               markerfacecolor=COLORS['purple'], markeredgecolor=COLORS['purple'],
               markeredgewidth=1.5, markersize=8, label='V2', linestyle='none'),
        Line2D([0], [0], marker='o', color='none',
               markerfacecolor=COLORS['orange'], markeredgecolor=COLORS['orange'],
               markeredgewidth=1.5, markersize=8, label='JDK', linestyle='none'),
    ]

    leg = ax.legend(
        handles=legend_elements,
        loc='upper center',
        bbox_to_anchor=(0.5, -0.26),
        ncol=2,
        frameon=False,
        fontsize=12,
        handlelength=1.5,
        handletextpad=0.6,
        columnspacing=2.0
    )

    for text in leg.get_texts():
        text.set_color('white')
        text.set_fontsize(12)

    plt.tight_layout(rect=[0, 0.18, 1, 1])
    fig.savefig(
        output_path,
        dpi=DPI,
        transparent=True,
        bbox_inches='tight',
        facecolor='none',
        edgecolor='none'
    )
    plt.close(fig)


# ──────────────────────────────────────────────────────────────────────────────
# Main
# ──────────────────────────────────────────────────────────────────────────────

def main():
    for path in (V2_CSV_PATH, JDK_CSV_PATH):
        if not os.path.exists(path):
            print(f"Error: Required file '{path}' not found.")
            sys.exit(1)

    os.makedirs(OUTPUT_DIR, exist_ok=True)

    print(f"Loading {V2_CSV_PATH} (V2)...")
    v2_data = load_wide_jmh_csv(V2_CSV_PATH)
    print(f"  Loaded {len(v2_data)} sizes")

    print(f"Loading {JDK_CSV_PATH} (JDK)...")
    jdk_data = load_wide_jmh_csv(JDK_CSV_PATH)
    print(f"  Loaded {len(jdk_data)} sizes")

    canonical_sizes = sorted(list(set(v2_data.keys()) | set(jdk_data.keys())))
    print(f"\nUsing {len(canonical_sizes)} unified sizes: {canonical_sizes}")

    print(f"\nGenerating comparison charts...")
    print(f"  Purple = V2")
    print(f"  Orange = JDK LinkedHashSet\n")

    for file_slug, col_name in OPERATIONS.items():
        output_path = os.path.join(OUTPUT_DIR, f'{file_slug}.png')
        create_chart(col_name, col_name, v2_data, jdk_data,
                     canonical_sizes, output_path)
        print(f"  ✓ {file_slug}.png ({col_name})")

    print(f"\n✓ All comparison charts saved to {OUTPUT_DIR}")


if __name__ == '__main__':
    main()