import socket

# Create a TCP/IP socket
stream_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Define host and port
host = 'localhost'
port = 8080

# Connect the socket to the server
server_address = (host, port)
print("Connecting to server...")
stream_socket.connect(server_address)

try:
    while True:
        # Send data
        message = input("Enter message to send (or 'q' to quit): ")
        if message.lower() == 'q':
            break
        stream_socket.sendall(message.encode())

        # Receive response from the server
        data = stream_socket.recv(16)
        print('Received from server:', data.decode())
except Exception as e:
    print(f"An error occurred: {e}")
finally:
    # Close the socket
    stream_socket.close()
    print('Socket closed')
