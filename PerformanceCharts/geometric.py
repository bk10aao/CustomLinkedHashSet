#!/usr/bin/env python3
"""
Generate a geometric mean relative performance comparison chart between CustomLinkedHashSet and JDK LinkedHashSet.
Compatible with wide-format JMH CSV performance reports. Uncapped version with independent axis limits.
"""

import matplotlib

matplotlib.use('Agg')
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from scipy.stats import gmean


# ──────────────────────────────────────────────────────────────────────────────
# CSV Loading (Robust handling for mixed comma/semicolon delimiters)
# ──────────────────────────────────────────────────────────────────────────────

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
custom_df = load_wide_jmh_csv('CustomLinkedHashSet_jmh_performance.csv')
jdk_df = load_wide_jmh_csv('LinkedHashSet_jmh_performance.csv')

# Ensure 'Size' column is numeric
custom_df['Size'] = pd.to_numeric(custom_df['Size'])
jdk_df['Size'] = pd.to_numeric(jdk_df['Size'])

# Set 'Size' as the index so columns represent operations
custom_pivot = custom_df.set_index('Size')
jdk_pivot = jdk_df.set_index('Size')

common_sizes = sorted(
    list(set(custom_pivot.index).intersection(set(jdk_pivot.index)))
)
custom_pivot = custom_pivot.loc[common_sizes]
jdk_pivot = jdk_pivot.loc[common_sizes]

# Exclude putIfAbsent or similar if present, keeping all set benchmarks
benchmarks = [
    b
    for b in custom_pivot.columns
    if b in jdk_pivot.columns and 'putIfAbsent' not in b
]

custom_fixed = custom_pivot.copy()
jdk_fixed = jdk_pivot.copy()
for b in benchmarks:
    custom_fixed[b] = (
        pd.to_numeric(custom_fixed[b], errors='coerce').fillna(1).replace(0, 1)
    )
    jdk_fixed[b] = (
        pd.to_numeric(jdk_fixed[b], errors='coerce').fillna(1).replace(0, 1)
    )

ratios = []
labels = []
colors = []

jdk_win_color = '#FF4D4D'
custom_win_color = '#4DA6FF'

for b in benchmarks:
    custom_vals = custom_fixed[b].dropna()
    jdk_vals = jdk_fixed[b].dropna()

    if custom_vals.empty or jdk_vals.empty:
        continue

    # Compute per-size speedup ratios (JDK time / Custom time)
    per_size_ratios = jdk_vals / custom_vals
    g_ratio = gmean(per_size_ratios)

    if g_ratio >= 1.0:
        ratios.append(g_ratio - 1)
        colors.append(custom_win_color)
    else:
        speedup = 1.0 / g_ratio
        ratios.append(-(speedup - 1))
        colors.append(jdk_win_color)

    # Clean up operation name for display
    clean_label = (
        b.replace('(K,V)', '')
        .replace('(K)', '')
        .replace('(V)', '')
        .replace('(Object o)', '')
        .replace('(Object)', '')
        .replace('()', '')
    )
    labels.append(clean_label)

if not ratios:
    raise ValueError(
        'No common benchmarks with valid data found between the two CSV files.'
    )

sorted_indices = np.argsort(ratios)
sorted_ratios = [ratios[idx] for idx in sorted_indices]
sorted_labels = [labels[idx] for idx in sorted_indices]
sorted_colors = [colors[idx] for idx in sorted_indices]

# Determine independent dynamic axis limits for left (JDK wins) and right (Custom wins) sides
negative_ratios = [r for r in sorted_ratios if r < 0]
positive_ratios = [r for r in sorted_ratios if r > 0]

max_left_val = abs(min(negative_ratios)) if negative_ratios else 0.25
max_right_val = max(positive_ratios) if positive_ratios else 0.25

# Add a clean padding buffer (approx 20% extra space for text labels)
left_limit = -(max_left_val * 1.25)
right_limit = max_right_val * 1.25

fig_height = max(6, len(sorted_labels) * 0.45)
fig, ax = plt.subplots(figsize=(12, fig_height), facecolor='none')
ax.set_facecolor('none')

bars = ax.barh(
    range(len(sorted_labels)),
    sorted_ratios,
    color=sorted_colors,
    alpha=0.9,
    height=0.6,
)
ax.axvline(x=0, color='#ffffff', linewidth=1.2)

ax.set_xlim(left_limit, right_limit)

# Generate independent ticks for left and right sides from zero
left_ticks = np.arange(np.floor(left_limit * 4) / 4.0, 0, 0.25)
right_ticks = np.arange(0.25, right_limit + 0.001, 0.5 if right_limit > 1.5 else 0.25)
ticks = np.concatenate([left_ticks, [0], right_ticks])

tick_labels = []
for t in ticks:
    if abs(t) < 0.01:
        tick_labels.append('Tie')
    else:
        factor = abs(t) + 1
        side = 'JDK' if t < 0 else 'Custom'
        tick_labels.append(f'{factor:.2f}x {side}' if t == ticks[0] or t == ticks[-1] else f'{factor:.2f}x')

ax.set_xticks(ticks)
ax.set_xticklabels(tick_labels, color='#ffffff', fontsize=10)

ax.set_ylim(-0.5, len(sorted_labels) - 0.5)
ax.set_yticks(range(len(sorted_labels)))
ax.set_yticklabels(sorted_labels, color='#ffffff', fontsize=10)

# Add exact numeric speedup values directly next to each bar
for idx, (bar, r) in enumerate(zip(bars, sorted_ratios)):
    val = abs(r)
    if val < 0.02:
        text_str = 'Tie'
    else:
        factor = val + 1
        text_str = f'{factor:.2f}x'

    if r >= 0:
        ax.text(
            r + 0.02,
            idx,
            f'  {text_str}',
            va='center',
            ha='left',
            color='#ffffff',
            fontsize=9,
            fontweight='bold',
        )
    else:
        ax.text(
            r - 0.02,
            idx,
            f'{text_str}  ',
            va='center',
            ha='right',
            color='#ffffff',
            fontsize=9,
            fontweight='bold',
        )

ax.set_title(
    (
        'Overall Relative Performance Comparison (CustomLinkedHashSet vs JDK'
        ' LinkedHashSet)\n(Geometric Mean Across All Sizes - Uncapped & Asymmetric Scaling)'
    ),
    fontsize=14,
    fontweight='bold',
    pad=15,
    color='#ffffff',
)
ax.set_xlabel(
    '← JDK Faster  |  Relative Speedup Factor (Uncapped)  |  Custom Faster →',
    fontsize=12,
    labelpad=10,
    color='#ffffff',
)

ax.grid(True, axis='x', linestyle='--', alpha=0.3, color='#888888')
ax.tick_params(colors='#ffffff', which='both', length=0)

for spine in ax.spines.values():
    spine.set_edgecolor('#555555')

plt.tight_layout()
plt.savefig('geometric.png', dpi=300, transparent=True)
plt.close()
print('Generated asymmetric uncapped geometric comparison graph successfully!')