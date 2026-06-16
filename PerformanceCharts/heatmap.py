import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

clhs_df = pd.read_csv('CustomLinkedHashSet_performance_data.csv')
lhs_df = pd.read_csv('LinkedHashSet_performance_data.csv')

sizes = clhs_df['Size'].tolist()
methods = [c for c in clhs_df.columns if c != 'Size']

heatmap_data = np.zeros((len(methods), len(sizes)))
text_labels = []

for i, m in enumerate(methods):
    row_labels = []
    for j, size in enumerate(sizes):
        clhs_val = clhs_df.loc[clhs_df['Size'] == size, m].values[0]
        lhs_val = lhs_df.loc[lhs_df['Size'] == size, m].values[0]

        if clhs_val == 0: clhs_val = 1
        if lhs_val == 0: lhs_val = 1

        # log2 ratio: positive means Custom is faster (LinkedHashSet took more time)
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

# Plotting the heatmap
fig, ax = plt.subplots(figsize=(16, 14), facecolor='none')
ax.set_facecolor('none')

clipped_data = np.clip(heatmap_data, -4.0, 4.0)
cmap = sns.diverging_palette(15, 240, as_cmap=True)

sns.heatmap(clipped_data,
            annot=text_labels,
            fmt="",
            cmap=cmap,
            center=0,
            xticklabels=sizes,
            yticklabels=sorted_methods,
            ax=ax,
            cbar_kws={
                'label': '← JDK Faster (LinkedHashSet)  |  Relative Speedup Scale (Clipped at 16x)  |  Custom Faster (CustomLinkedHashSet) →'},
            linewidths=0.6,
            linecolor='#444444',
            annot_kws={'size': 9, 'weight': 'bold'})

ax.set_title(
    'Java LinkedHashSet Performance Comparison Matrix Heatmap\n(Positive/Blue = CustomLinkedHashSet Faster, Negative/Red = LinkedHashSet Faster)',
    color='#ffffff', fontsize=16, fontweight='bold', pad=20)
ax.set_ylabel('Set Interface Methods', color='#aaaaaa', fontsize=13, labelpad=10)
ax.set_xlabel('Collection Size (Elements)', color='#aaaaaa', fontsize=13, labelpad=10)

ax.tick_params(colors='#ffffff', labelsize=11)
plt.xticks(rotation=45)
plt.yticks(rotation=0)

cbar = ax.collections[0].colorbar
cbar.ax.tick_params(colors='#ffffff', labelsize=10)
cbar.ax.yaxis.label.set_color('#ffffff')
cbar.ax.yaxis.label.set_fontsize(12)

plt.tight_layout()
plt.savefig('heatmap.png', dpi=300, transparent=True)
plt.close()

print("Heatmap saved successfully as custom_linked_hash_set_performance_heatmap.png")
print("Top 3 worst performing methods for Custom on average:")
print(sorted_methods[:3])
print("Top 3 best performing methods for Custom on average:")
print(sorted_methods[-3:])