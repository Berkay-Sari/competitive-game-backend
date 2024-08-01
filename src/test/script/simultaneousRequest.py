import concurrent.futures
import requests

# URL of the endpoint
url = 'http://localhost:8080/users/update-level/2'

# Function to send a single PUT request
def send_put_request():
    response = requests.put(url)
    return response.status_code, response.text

# Main function to send PUT requests concurrently
def main():
    with concurrent.futures.ThreadPoolExecutor(max_workers=1000) as executor:
        futures = [executor.submit(send_put_request) for _ in range(1000)]
        for future in concurrent.futures.as_completed(futures):
            status, text = future.result()
            print(f'Status: {status}, Response: {text}')

if __name__ == '__main__':
    main()
