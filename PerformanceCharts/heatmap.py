#!/usr/bin/env python3
"""
Generate a performance comparison matrix heatmap between CustomLinkedHashSet and JDK LinkedHashSet.
Compatible with wide-format JMH CSV performance reports.
"""

import matplotlib

matplotlib.use('Agg')
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import seaborn as sns
import io


# ──────────────────────────────────────────────────────────────────────────────
# CSV Loading (Robust handling for mixed/semicolon/comma delimiters)
# ──────────────────────────────────────────────────────────────────────────────

def load_wide_jmh_csv(filepath):
    """Load wide-format JMH CSV robustly regardless of delimiter."""
    with open(filepath, 'r') as f:
        lines = [line.strip() for line in f if line.strip()]

    sample_line = lines[0]
    sep = ';' if ';' in sample_line else ','

    df = pd.read_csv(io.StringIO('\n'.join(lines)), sep=sep)
    df.columns = [c.strip() for c in df.columns]
    return df


# Load datasets
clhs_df = load_wide_jmh_csv('CustomLinkedHashSet_jmh_performance.csv')
lhs_df = load_wide_jmh_csv('LinkedHashSet_jmh_performance.csv')

# Ensure 'Size' is numeric and clean up method columns
clhs_df['Size'] = pd.to_numeric(clhs_df['Size'])
lhs_df['Size'] = pd.to_numeric(lhs_df['Size'])

sizes = sorted(clhs_df['Size'].tolist())
methods = [c for c in clhs_df.columns if c != 'Size' and c in lhs_df.columns and 'putIfAbsent' not in c]

heatmap_data = np.zeros((len(methods), len(sizes)))
text_labels = []

for i, m in enumerate(methods):
    row_labels = []
    for j, size in enumerate(sizes):
        c_val_str = clhs_df.loc[clhs_df['Size'] == size, m].values
        j_val_str = lhs_df.loc[lhs_df['Size'] == size, m].values

        clhs_val = float(c_val_str[0]) if len(c_val_str) > 0 and pd.notna(c_val_str[0]) else 1.0
        lhs_val = float(j_val_str[0]) if len(j_val_str) > 0 and pd.notna(j_val_str[0]) else 1.0

        if clhs_val == 0: clhs_val = 1.0
        if lhs_val == 0: lhs_val = 1.0

        # log2 ratio: positive means Custom is faster (JDK took more time)
        ratio = np.log2(lhs_val / clhs_val)
        heatmap_data[i, j] = ratio

        if lhs_val >= clhs_val:
            factor = lhs_val / clhs_val
            row_labels.append(f"+{factor:.1f}x" if factor < 100 else f"+{factor:.0f}x")
        else:
            factor = clhs_val / lhs_val
            row_labels.append(f"-{factor:.1f}x" if factor < 100 else f"-{factor:.0f}x")
    text_labels.append(row_labels)

text_labels = np.array(text_labels)

# Sort methods by average performance ratio
avg_ratios = np.mean(heatmap_data, axis=1)
sorted_idx = np.argsort(avg_ratios)

heatmap_data = heatmap_data[sorted_idx]
text_labels = text_labels[sorted_idx]
sorted_methods = [methods[idx] for idx in sorted_idx]

# Clean method names for display
display_methods = [
    m.replace('(K,V)', '')
    .replace('(K)', '')
    .replace('(V)', '')
    .replace('(Object o)', '')
    .replace('(Object)', '')
    .replace('()', '')
    for m in sorted_methods
]

# Plotting the heatmap
fig, ax = plt.subplots(figsize=(16, 12), facecolor='none')
ax.set_facecolor('none')

clipped_data = np.clip(heatmap_data, -4.0, 4.0)
cmap = sns.diverging_palette(15, 240, as_cmap=True)

sns.heatmap(clipped_data,
            annot=text_labels,
            fmt="",
            cmap=cmap,
            center=0,
            xticklabels=[f'{s:,}' for s in sizes],
            yticklabels=display_methods,
            ax=ax,
            cbar_kws={
                'label': '← JDK Faster (LinkedHashSet)  |  Relative Speedup Scale (Clipped at 16x)  |  Custom Faster →'},
            linewidths=0.6,
            linecolor='#444444',
            annot_kws={'size': 9, 'weight': 'bold'})

ax.set_title(
    'Java LinkedHashSet Performance Comparison Matrix Heatmap\n(Positive/Blue = CustomLinkedHashSet Faster, Negative/Red = LinkedHashSet Faster)',
    color='#ffffff', fontsize=15, fontweight='bold', pad=20)
ax.set_ylabel('Set Interface Methods', color='#ffffff', fontsize=12, labelpad=10)
ax.set_xlabel('Collection Size (Elements)', color='#ffffff', fontsize=12, labelpad=10)

ax.tick_params(colors='#ffffff', labelsize=10)
plt.xticks(rotation=45, ha='right')
plt.yticks(rotation=0)

cbar = ax.collections[0].colorbar
cbar.ax.tick_params(colors='#ffffff', labelsize=10)
cbar.ax.yaxis.label.set_color('#ffffff')
cbar.ax.yaxis.label.set_fontsize(11)

plt.tight_layout()
plt.savefig('heatmap.png', dpi=300, transparent=True)
plt.close()

print("Heatmap saved successfully as heatmap.png")
print("Top 3 worst performing methods for Custom on average:")
print(display_methods[:3])
print("Top 3 best performing methods for Custom on average:")
print(display_methods[-3:])