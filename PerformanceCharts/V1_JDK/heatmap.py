#!/usr/bin/env python3
"""
Generate a performance comparison matrix heatmap between CustomLinkedSet V1 and JDK LinkedHashSet.
Compatible with wide-format JMH CSV performance reports.
"""

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import seaborn as sns
import io

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
custom_df = load_wide_jmh_csv('CustomLinkedSet_jmh_performanceV1.csv')   # Custom V1
jdk_df    = load_wide_jmh_csv('LinkedHashSet_jmh_performance.csv')       # JDK LinkedHashSet

custom_df['Size'] = pd.to_numeric(custom_df['Size'])
jdk_df['Size']    = pd.to_numeric(jdk_df['Size'])

sizes = sorted(custom_df['Size'].tolist())
methods = [c for c in custom_df.columns if c != 'Size' and c in jdk_df.columns]

heatmap_data = np.zeros((len(methods), len(sizes)))
text_labels = []

for i, m in enumerate(methods):
    row_labels = []
    for j, size in enumerate(sizes):
        custom_vals = custom_df.loc[custom_df['Size'] == size, m].values
        jdk_vals    = jdk_df.loc[jdk_df['Size'] == size, m].values

        custom_val = float(custom_vals[0]) if len(custom_vals) > 0 and pd.notna(custom_vals[0]) else 1.0
        jdk_val    = float(jdk_vals[0])    if len(jdk_vals) > 0 and pd.notna(jdk_vals[0]) else 1.0

        if custom_val == 0: custom_val = 1.0
        if jdk_val == 0:    jdk_val = 1.0

        # log2 ratio: positive means Custom V1 is faster (JDK took more time)
        ratio = np.log2(jdk_val / custom_val)
        heatmap_data[i, j] = ratio

        if jdk_val >= custom_val:
            factor = jdk_val / custom_val
            row_labels.append(f"+{factor:.1f}x" if factor < 100 else f"+{factor:.0f}x")
        else:
            factor = custom_val / jdk_val
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
     .replace('(Collection)', '')
     .replace('(int,float)', '')
     .replace('(int)', '')
     .replace('(T[])', '')
     .replace('()', '')
    for m in sorted_methods
]

# Plotting the heatmap
fig, ax = plt.subplots(figsize=(16, 12), facecolor='none')
ax.set_facecolor('none')

clipped_data = np.clip(heatmap_data, -4.0, 4.0)
cmap = sns.diverging_palette(15, 240, as_cmap=True)

sns.heatmap(
    clipped_data,
    annot=text_labels,
    fmt="",
    cmap=cmap,
    center=0,
    xticklabels=[f'{s:,}' for s in sizes],
    yticklabels=display_methods,
    ax=ax,
    cbar_kws={
        'label': '← JDK Faster  |  Relative Speedup Scale (Clipped at 16x)  |  V1 Faster →'
    },
    linewidths=0.6,
    linecolor='#444444',
    annot_kws={'size': 9, 'weight': 'bold'}
)

ax.set_title(
    'V1 vs JDK LinkedHashSet\n'
    'Performance Comparison Matrix Heatmap\n'
    '(Positive/Blue = V1 Faster, Negative/Red = JDK Faster)',
    color='#ffffff', fontsize=15, fontweight='bold', pad=20
)
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
print("Top 3 worst performing methods for Custom V1 on average:")
print(display_methods[:3])
print("Top 3 best performing methods for Custom V1 on average:")
print(display_methods[-3:])