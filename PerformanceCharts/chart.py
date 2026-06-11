import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.ticker as ticker
from matplotlib.lines import Line2D

# Load datasets
custom_df = pd.read_csv('CustomLinkedHashSet_performance_data.csv')
native_df = pd.read_csv('LinkedHashSet_performance_data.csv')

# Get common columns excluding 'Size'
common_cols = sorted([col for col in custom_df.columns if col in native_df.columns and col not in ['Size']])

# Filter for columns with valid numerical data
valid_cols = [col for col in common_cols if pd.notna(custom_df[col].mean()) and pd.notna(native_df[col].mean())]

# Theme colors
color_custom = '#ff4d4d'  # Red
color_native = '#4da6ff'  # Blue

for method in valid_cols:
    fig, ax = plt.subplots(figsize=(8, 5.5))

    # Mask out skipped test rows marked as -1
    mask_custom = custom_df[method] != -1
    mask_native = native_df[method] != -1

    # Plot data lines
    ax.plot(custom_df['Size'][mask_custom], custom_df[method][mask_custom], color=color_custom, marker='o',
            markersize=5, linestyle='-', linewidth=2)
    ax.plot(native_df['Size'][mask_native], native_df[method][mask_native], color=color_native, marker='o',
            markersize=5, linestyle='-', linewidth=2)

    # Absolute bounding limits matching the original template style
    ax.set_xlim(left=10000)

    # Force the Y-axis baseline to explicitly start at 0
    ax.set_ylim(bottom=0)

    # Render headings and labels in white text
    ax.set_title(method, fontsize=14, fontweight='bold', color='white', pad=15)
    ax.set_xlabel('Size', fontsize=11, color='white')
    ax.set_ylabel('Time (ns)', fontsize=11, color='white')

    # --- Y-AXIS OPTIMIZATIONS ---
    # Disable scientific notation auto-scaling to avoid hidden multipliers
    ax.ticklabel_format(style='plain', axis='y')
    # Format the numbers explicitly as integers with thousands-separators (commas)
    ax.yaxis.set_major_formatter(ticker.FuncFormatter(lambda x, p: format(int(x), ',')))
    # Ensure any residual scientific/offset text is explicitly colored white
    ax.yaxis.get_offset_text().set_color('white')
    ax.yaxis.get_offset_text().set_fontsize(10)

    ax.tick_params(axis='both', colors='white')
    ax.grid(True, linestyle='--', alpha=0.3, color='white')

    for spine in ax.spines.values():
        spine.set_color('white')

    # Build clear legend point labels
    legend_elements = [
        Line2D([0], [0], marker='o', color='none', label='CustomLinkedHashSet',
               markerfacecolor=color_custom, markeredgecolor=color_custom, markersize=8, linestyle='None'),
        Line2D([0], [0], marker='o', color='none', label='LinkedHashSet',
               markerfacecolor=color_native, markeredgecolor=color_native, markersize=8, linestyle='None')
    ]

    legend = ax.legend(handles=legend_elements, loc='upper center', bbox_to_anchor=(0.5, -0.15),
                       fontsize=10, frameon=False, ncol=2)
    for text in legend.get_texts():
        text.set_color('white')

    # Maintain transparent styling
    fig.patch.set_alpha(0.0)
    ax.patch.set_alpha(0.0)

    plt.tight_layout()

    # Sanitize characters that are illegal in file names (e.g., <, >, ?, spaces)
    safe_filename = (method.replace('(', '_')
                     .replace(')', '_')
                     .replace(',', '_')
                     .replace('.', '_')
                     .replace('<', '')
                     .replace('>', '')
                     .replace('?', 'any')
                     .replace(' ', ''))

    # Save output plot image
    plt.savefig(f'plot_{safe_filename}.png', transparent=True, bbox_inches='tight')
    plt.close()

print(f"Successfully processed {len(valid_cols)} performance charts with clean y-axis formatting starting at 0.")