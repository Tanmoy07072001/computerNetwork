import random

def generate_random_sequence(length):
    # Generate a random sequence of 0s and 1s of the given length
    return ''.join(str(random.randint(0, 1)) for _ in range(length))

def write_random_sequence_to_file(filename, length):
    # Generate the random sequence
    random_sequence = generate_random_sequence(length)
    
    # Write the sequence to the specified file
    with open(filename, 'w') as file:
        file.write(random_sequence)

if __name__ == "__main__":
    try:
        length = int(input("Enter the length of the random sequence: "))
        filename = input("Enter the name of the file to save the sequence: ")

        write_random_sequence_to_file(filename, length)
        print(f"Random sequence of length {length} has been written to {filename}.")
    
    except ValueError:
        print("Invalid input. Please enter a valid length as an integer.")
