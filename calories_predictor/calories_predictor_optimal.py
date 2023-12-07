import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim

import pandas as pd
import numpy as np 
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split
import seaborn as sns


exercise_data = pd.read_csv( './exercise.csv' )
calories_data = pd.read_csv( './calories.csv' )


dataset=exercise_data.join( calories_data.set_index( 'User_ID' ), on='User_ID', how='left')

print(dataset.describe())

train_frame, test_frame = train_test_split(dataset, test_size=0.2)


class CaloriesExercisesDatasetOneFeature(torch.utils.data.Dataset):
    def __init__(self, dataset):
        self._df = dataset

    def __len__(self): 
        return len(self._df)

    def __getitem__(self, idx): #[]
        item = self._df.iloc[idx]
        return {'features': torch.tensor([item["Duration"]],dtype=torch.float32),
                'labels': torch.tensor([item['Calories']],dtype=torch.float32)}
    
        
class CaloriesExercisesDatasetMultipleFeature(torch.utils.data.Dataset):
    def __init__(self, dataset):
        self._df = dataset

    def __len__(self): 
        return len(self._df)

    def __getitem__(self, idx): #[]
        item = self._df.iloc[idx]
        return {'features': torch.tensor([item['Duration'],item['Weight'],item['Height'],item['Age']],dtype=torch.float32),
                'labels': torch.tensor([item['Calories']],dtype=torch.float32)}
        
               


train_set = CaloriesExercisesDatasetOneFeature(train_frame)
test_set = CaloriesExercisesDatasetOneFeature(test_frame)

class PolynomialRegressionModel(nn.Module):
    def __init__(self, degree, in_features=1, out_features=1):
        super().__init__()
        self.degree = degree
        self.linear = nn.Linear(in_features * degree, out_features)

    def forward(self, x):
        poly_x = torch.cat([x ** i for i in range(1, self.degree + 1)], dim=1)
        out = self.linear(poly_x)
        return out
    
model = PolynomialRegressionModel(2) # generez o instanță a modelului
# model = PolynomialRegressionModel(2,in_features=4)

LEARNING_RATE = 1e-8# Rata de invatare
# LEARNING_RATE = 1e-12 # Rata de invatare
NR_EPOCHS = 5 # Numarul de epoci
BATCH_SIZE = 32 # Numarul de samples dintr-un batch

criterion = nn.MSELoss()


optimizer = optim.SGD(model.parameters(), lr=LEARNING_RATE)

log_info = []

train_loader = torch.utils.data.DataLoader(train_set, batch_size=BATCH_SIZE, shuffle=True)

model.train() 



########### Training Loop #############

# pentru fiecare epoca (1 epoca = o iteratie peste intregul set de date)
for epoch in range(NR_EPOCHS):
    print('Running epoch {}'.format(epoch))

    epoch_losses = []
    
    # pentru fiecare batch de BATCH_SIZE exemple din setul de date    
    for i, batch in enumerate(train_loader):

        inputs, labels = batch['features'], batch['labels']
        
        optimizer.zero_grad()

        outputs = model(inputs)

        loss = criterion(outputs, labels)

        loss.backward()

        optimizer.step()

        epoch_losses.append(loss.item()) 
    log_info.append((epoch, np.mean(epoch_losses)))
    
# X = [x for x, loss in log_info]
# Y = [loss for x, loss in log_info]
# plt.plot(X,Y)
# plt.xlabel("Epoch")
# plt.ylabel("LOSS")

# plt.show()



test_loader = torch.utils.data.DataLoader(test_set, batch_size=32, shuffle=False)

eval_outputs = []
true_labels = []
x = []


model.eval()



########### Evaluation Loop #############
with torch.no_grad():
    for batch in test_loader:
        inputs, labels = batch['features'], batch['labels']
        # calculate outputs by running images through the network
        outputs = model(inputs)
        eval_outputs += outputs.squeeze(dim=1).tolist()
        true_labels += labels.squeeze(dim=1).tolist()
        x += inputs.squeeze(dim=1).tolist()

plt.figure(figsize=(8,6), dpi=100)
plt.plot(x, true_labels, 'o', label='Ground Truth')
plt.plot(x, eval_outputs, 'o', label='Prediction')
plt.legend()
plt.show()
