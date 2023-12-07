from flask import Flask, request
import torch
from flask.json import jsonify
from calories_predictor import PolynomialRegressionModel

app = Flask(__name__)

@app.route('/predict', methods=['POST'])
def predict():
    data = request.get_json() # get data from POST request
    model = PolynomialRegressionModel(2) # Initialize model
    model.load_state_dict(torch.load('model')) # Load saved parameters
    model.eval() # Set model to evaluation mode

    # Convert data to tensor
    features = torch.tensor(data['features'], dtype=torch.float32)

    # Make prediction
    prediction = model(features)

    return jsonify({  # Use jsonify to convert dict to JSON
        'prediction': prediction.item()
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)