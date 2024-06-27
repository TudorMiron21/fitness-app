import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression
import matplotlib.pyplot as plt
from sklearn.metrics import r2_score, mean_absolute_error, mean_squared_error
import joblib

exercise_data = pd.read_csv('./exercise.csv')
calories_data = pd.read_csv('./calories.csv')
dataset = exercise_data.join(calories_data.set_index('User_ID'), on='User_ID', how='left')

#transform categorical to numerical
dataset['Gender'] = dataset['Gender'].astype('category')
dataset['Gender'] = dataset['Gender'].cat.codes

# print(dataset.head())

# print(dataset.isnull().sum())

X = dataset.drop(columns=['Heart_Rate','Body_Temp','Calories','User_ID'])
y = dataset['Calories']

print(X)
print(y)

X_train,X_test,y_train,y_test = train_test_split(X,y,test_size=0.3)

lr = LinearRegression()

lr.fit(X_train,y_train)

c = lr.intercept_
m = lr.coef_

y_pred_train = lr.predict(X_train)


###TESTING
# Calculate R-squared score
r2 = r2_score(y_train, y_pred_train)
print(f"R-squared score: {r2}")

# Calculate Mean Absolute Error (MAE)
mae = mean_absolute_error(y_train, y_pred_train)
print(f"Mean Absolute Error (MAE): {mae}")

# Calculate Mean Squared Error (MSE)
mse = mean_squared_error(y_train, y_pred_train)
print(f"Mean Squared Error (MSE): {mse}")

# Calculate Root Mean Squared Error (RMSE)
rmse = mean_squared_error(y_train, y_pred_train, squared=False)
print(f"Root Mean Squared Error (RMSE): {rmse}")
###TESTING


joblib.dump(lr, 'linear_regression_model.pkl')
print("Model saved successfully.")