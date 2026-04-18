import requests
# 1. SET THE URL (Change this based on what you want to test)
# Options: /register, /login, /book
BASE_URL = "http://127.0.0.1:5000"
endpoint = "/login" 

# 2. SET THE DATA
# Make sure the keys ('email', 'password', etc.) match your app.py exactly
data = {
    "email": "anush2@test.com", 
    "password": "mypassword123"
}

# 3. THE EXECUTION
try:
    print(f"Sending request to {endpoint}...")
    response = requests.post(f"{BASE_URL}{endpoint}", json=data)
    
    print("--- SERVER RESPONSE ---")
    print("Status Code:", response.status_code)
    print("Response JSON:", response.json())

except Exception as e:
    print("--- CONNECTION ERROR ---")
    print("Could not connect to the server. Is app.py running?")
    print("Error details:", e)