import random

def mod_exp(base, exponent, modulus):
    """Computes (base^exponent) % modulus using modular exponentiation."""
    result = 1
    while exponent > 0:
        if exponent % 2 == 1:
            result = (result * base) % modulus
        base = (base * base) % modulus
        exponent //= 2
    return result

def diffie_hellman(p, g):
    # Generate secret key
    SA = random.randint(2, p - 2) 
    SB = random.randint(2, p - 2) 

    TA = mod_exp(g, SA, p)
    TB = mod_exp(g, SB, p)

    secret_key_Alice = mod_exp(TB, SA, p)
    secret_key_Bob = mod_exp(TA, SB, p)

    print(f"Alice's secret number is: {SA}")
    print(f"Bob's secret number is: {SB}")
    print(f"Alice's TA is: {TA}")
    print(f"Bob's TB is: {TB}")
    print("Computing shared secret keys...")
    print(f"Alice's generated secret key is: {secret_key_Alice}")
    print(f"Bob's generated secret key is: {secret_key_Bob}")

if __name__ == "__main__":
    p, g = map(int, input("Enter the prime numbers p and g: ").split())
    print("Generating keys for Alice and Bob...")
    diffie_hellman(p, g)
