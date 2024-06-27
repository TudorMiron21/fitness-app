from flask import Flask, request
import torch
from flask.json import jsonify
from calories_predictor import PolynomialRegressionModel
import joblib
import numpy as np

app = Flask(__name__)

# @app.route('/predict', methods=['POST'])
# def predict():
#     data = request.get_json() # get data from POST request
#     model = PolynomialRegressionModel(2) # Initialize model
#     model.load_state_dict(torch.load('model')) # Load saved parameters
#     model.eval() # Set model to evaluation mode

#     # Convert data to tensor
#     features = torch.tensor(data['features'], dtype=torch.float32)

#     # Make prediction
#     prediction = model(features)

#     return jsonify({  # Use jsonify to convert dict to JSON
#         'prediction': prediction.item()
#     })

model = joblib.load('linear_regression_model.pkl')
print("Model loaded successfully.")

@app.route('/predict', methods=['POST'])
def predict():
    data = request.get_json()  # get data from POST request

    # Extract features from the JSON data
    features = np.array(data['features']).reshape(1, -1)  # Reshape to 2D array

    # Make prediction
    prediction = model.predict(features)

    return jsonify({  # Use jsonify to convert dict to JSON
        'prediction': prediction[0]
    })


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
    
# {
#     "features":[gender,age,height,weight,duration]
# }