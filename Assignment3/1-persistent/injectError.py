import random

class InjectError:
    def __init__(self, payload):
        self.payload = payload

    def inject_error(self):
        min_length = len(self.payload)

        num_errors = random.randint(0, 2) 

        for _ in range(num_errors):
            index = random.randint(0, min_length - 1)  
            random_bit = random.choice(['0', '1'])  

            self.payload = self.payload[:index] + random_bit + self.payload[index + 1:]

            # print(f"Changed in both strings: Index={index}, New Bit={random_bit}")
        return self.payload

# Example usage
if __name__ == "__main__":
    payload = '11000101101100101101001001001001010100101101011110000000'
    error_injector = InjectError(payload)
    changed_payload = error_injector.inject_error()
    print("Modified Array:", changed_payload)
