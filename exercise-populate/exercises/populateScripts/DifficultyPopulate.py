from sqlalchemy import create_engine, MetaData, Table
from sqlalchemy.orm import sessionmaker
from sqlalchemy.exc import SQLAlchemyError

DATABASE_URI = 'postgresql://postgres:root@localhost:5432/authorization'

# Establish the connection to the database
engine = create_engine(DATABASE_URI)

metadata = MetaData()
metadata.reflect(bind=engine)

# Assuming your table is named 'difficulty' as per the Java class
difficulty_table = Table('difficulty', metadata, autoload_with=engine)

# Create a Session
Session = sessionmaker(bind=engine)
session = Session()

# Example: Inserting new rows into the 'difficulty' table
difficulty_levels = [
    {'dificulty_level': 'Beginner', 'difficulty_level_number': 1.0},
    {'dificulty_level': 'Intermediate', 'difficulty_level_number': 2.0},
    {'dificulty_level': 'Expert', 'difficulty_level_number': 3.0}
]

# Insert data into the table
try:
    for level in difficulty_levels:
        # Construct an insert statement for the difficulty_table
        ins = difficulty_table.insert().values(**level)
        # Execute the statement
        session.execute(ins)
    # Commit the session if all inserts are successful
    session.commit()
    print("Data inserted successfully.")
except SQLAlchemyError as e:
    print(f"An error occurred while inserting data: {e}")
    session.rollback()  # Roll back the session on error
finally:
    session.close()  # Always close the session