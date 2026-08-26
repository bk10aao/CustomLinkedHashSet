#!/usr/bin/env python3
"""
Generate a geometric mean relative performance comparison chart between
CustomLinkedSet V1 and CustomLinkedSet V2 (excluding removeAll, retainAll and constructors).
Compatible with wide-format JMH CSV performance reports.
Uncapped version with independent axis limits.
"""

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from scipy.stats import gmean

def load_wide_jmh_csv(filepath):
    """Load wide-format CSV and return pandas DataFrame."""
    with open(filepath, 'r') as f:
        lines = [line.strip() for line in f if line.strip()]
    sample_line = lines[1] if len(lines) > 1 else lines[0]
    sep = ';' if ';' in sample_line else ','
    df = pd.read_csv(filepath, sep=sep)
    if len(df.columns) == 1 and len(lines) > 0:
        alt_sep = ',' if sep == ';' else ';'
        df = pd.read_csv(filepath, sep=alt_sep)
    df.columns = [c.strip() for c in df.columns]
    return df


# Load datasets
v1_df = load_wide_jmh_csv('../CustomLinkedSet_jmh_performanceV1.csv')
v2_df = load_wide_jmh_csv('../CustomLinkedSet_jmh_performanceV2.csv')

v1_df['Size'] = pd.to_numeric(v1_df['Size'])
v2_df['Size'] = pd.to_numeric(v2_df['Size'])

v1_pivot = v1_df.set_index('Size')
v2_pivot = v2_df.set_index('Size')

common_sizes = sorted(list(set(v1_pivot.index).intersection(set(v2_pivot.index))))
v1_pivot = v1_pivot.loc[common_sizes]
v2_pivot = v2_pivot.loc[common_sizes]

# Explicitly exclude the extreme outliers and constructors
EXCLUDE = {
    'removeAll(Collection)',
    'retainAll(Collection)',
    'Constructor()',
    'Constructor(int)',
    'Constructor(int,float)',
    'Constructor(Collection)',
}

benchmarks = [
    b for b in v1_pivot.columns
    if b in v2_pivot.columns and b not in EXCLUDE
]

v1_fixed = v1_pivot.copy()
v2_fixed = v2_pivot.copy()

for b in benchmarks:
    v1_fixed[b] = pd.to_numeric(v1_fixed[b], errors='coerce').fillna(1).replace(0, 1)
    v2_fixed[b] = pd.to_numeric(v2_fixed[b], errors='coerce').fillna(1).replace(0, 1)

ratios = []
labels = []
colors = []

v1_win_color = '#4DA6FF'   # Blue  → V1 faster
v2_win_color = '#FF4D4D'   # Red   → V2 faster

for b in benchmarks:
    v1_vals = v1_fixed[b].dropna()
    v2_vals = v2_fixed[b].dropna()
    if v1_vals.empty or v2_vals.empty:
        continue

    # V2 time / V1 time  →  >1 means V1 is faster
    per_size_ratios = v2_vals / v1_vals
    g_ratio = gmean(per_size_ratios)

    if g_ratio >= 1.0:
        ratios.append(g_ratio - 1)
        colors.append(v1_win_color)
    else:
        speedup = 1.0 / g_ratio
        ratios.append(-(speedup - 1))
        colors.append(v2_win_color)

    clean_label = (
        b.replace('(K,V)', '')
         .replace('(K)', '')
         .replace('(V)', '')
         .replace('(Object o)', '')
         .replace('(Object)', '')
         .replace('(Collection)', '')
         .replace('(int,float)', '')
         .replace('(int)', '')
         .replace('(T[])', '')
         .replace('()', '')
    )
    labels.append(clean_label)

if not ratios:
    raise ValueError('No common benchmarks with valid data found.')

sorted_indices = np.argsort(ratios)
sorted_ratios = [ratios[idx] for idx in sorted_indices]
sorted_labels = [labels[idx] for idx in sorted_indices]
sorted_colors = [colors[idx] for idx in sorted_indices]

negative_ratios = [r for r in sorted_ratios if r < 0]
positive_ratios = [r for r in sorted_ratios if r > 0]
max_left_val  = abs(min(negative_ratios)) if negative_ratios else 0.25
max_right_val = max(positive_ratios) if positive_ratios else 0.25

left_limit  = -(max_left_val * 1.25)
right_limit = max_right_val * 1.25

fig_height = max(6, len(sorted_labels) * 0.45)
fig, ax = plt.subplots(figsize=(12, fig_height), facecolor='none')
ax.set_facecolor('none')

bars = ax.barh(
    range(len(sorted_labels)),
    sorted_ratios,
    color=sorted_colors,
    alpha=0.9,
    height=0.65,
)

ax.axvline(x=0, color='#ffffff', linewidth=1.4, zorder=1)
ax.set_xlim(left_limit, right_limit)

left_ticks  = np.arange(np.floor(left_limit * 4) / 4.0, 0, 0.25)
right_ticks = np.arange(0.25, right_limit + 0.001, 0.5 if right_limit > 1.5 else 0.25)
ticks = np.concatenate([left_ticks, [0], right_ticks])

tick_labels = []
for t in ticks:
    if abs(t) < 0.01:
        tick_labels.append('Tie')
    else:
        factor = abs(t) + 1
        side = 'V2' if t < 0 else 'V1'
        tick_labels.append(
            f'{factor:.2f}x {side}' if t == ticks[0] or t == ticks[-1] else f'{factor:.2f}x'
        )

ax.set_xticks(ticks)
ax.set_xticklabels(tick_labels, color='#ffffff', fontsize=10)
ax.set_ylim(-0.5, len(sorted_labels) - 0.5)
ax.set_yticks(range(len(sorted_labels)))
ax.set_yticklabels(sorted_labels, color='#ffffff', fontsize=10)

for idx, r in enumerate(sorted_ratios):
    val = abs(r)
    text_str = 'Tie' if val < 0.02 else f'{val + 1:.2f}x'

    if r >= 0:
        ax.text(r + 0.02, idx, f'  {text_str}',
                va='center', ha='left', color='#ffffff', fontsize=9, fontweight='bold')
    else:
        ax.text(r - 0.02, idx, f'{text_str}  ',
                va='center', ha='right', color='#ffffff', fontsize=9, fontweight='bold')

ax.set_title(
    'Overall Relative Performance Comparison (CustomLinkedSet V1 vs V2)\n'
    '(Geometric Mean Across All Sizes – removeAll / retainAll / Constructors excluded)',
    fontsize=14, fontweight='bold', pad=15, color='#ffffff'
)

ax.set_xlabel(
    '← V2 Faster  |  Relative Speedup Factor (Uncapped)  |  V1 Faster →',
    fontsize=12, labelpad=10, color='#ffffff'
)

ax.grid(False)
ax.tick_params(colors='#ffffff', which='both', length=0)

for spine in ax.spines.values():
    spine.set_edgecolor('#555555')
    spine.set_linewidth(0.8)

plt.tight_layout()
plt.savefig('geometric.png', dpi=300, transparent=True, bbox_inches='tight')
plt.close()

print('Generated clean geometric comparison graph (outliers excluded) successfully!')