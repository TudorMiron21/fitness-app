import pandas as pd


df1 = pd.read_csv('exercise_image.csv')
df2 = pd.read_csv('megaGymDataset.csv')

# Assuming 'Exercise_Name' from df1 and 'Title' from df2 are what you want to match
# Normalize the names
df1['Exercise_Name'] = df1['Exercise_Name'].str.strip().str.lower()
df2['Title'] = df2['Title'].str.strip().str.lower()

# Merge the datasets on the exercise names (assuming the names are in 'Exercise_Name' and 'Title' columns)
merged_df = pd.merge(df1, df2, left_on='Exercise_Name', right_on='Title', how='inner')

merged_df['hasNoReps'] = ~merged_df['Type'].str.lower().eq('cardio')
merged_df['hasWeight'] = ~(merged_df['Type'].str.lower().eq('cardio') | merged_df['Equipment_y'].str.strip().eq('Body Only'))

merged_df.to_csv('common_exercises.csv', index=False)




