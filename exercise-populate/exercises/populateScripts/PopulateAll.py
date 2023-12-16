script_filenames = ['DifficultyPopulate.py', 'CategoryPopulate.py', 'MuscleGroupPopulate.py','EquipmentPopulate.py','ExercisePopulate.py']

for script in script_filenames:
    with open(script) as file:
        exec(file.read())