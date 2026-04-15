import requests

url = "http://127.0.0.1:5000/register"
data = {
    "name": "Anush",
    "email": "anush**2**@test.com",
    "password": "mypassword123"
}

try:
    response = requests.post(url, json=data)
    print("Status Code:", response.status_code)
    print("Response JSON:", response.json())
except Exception as e:
    print("Error:", e)