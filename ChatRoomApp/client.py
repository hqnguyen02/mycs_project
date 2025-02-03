import socket
import threading
import sys
import time

client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Attempt to connect to the server
try:
    client.connect(('127.0.0.1', 55000))
except Exception as e:
    print(f"Unable to connect to the server: {e}")
    sys.exit()

username = input("Enter your name: ")

# Global flag to control threads
running = True

# Timeout value for the recv call
RECV_TIMEOUT = 1

def receive():
    global running
    client.settimeout(RECV_TIMEOUT)
    while running:
        try:
            message = client.recv(1024).decode('ascii')
            if not message:
                print("Disconnected from the server.")
                break
            if message == 'USERNAME':
                client.send(username.encode('ascii'))
            else:
                print(message)
        except socket.timeout:
            continue
        except Exception as e:
            if running:
                print(f"Client is disconnecting. {e}")
            break
    running = False

def write():
    global running
    while running:
        try:
            message = input("")
            if message.lower() == "exit":
                print("Exiting...")
                client.send(f"{username} has left the chat.".encode('ascii'))
                running = False
                break
            else:
                client.send(f"{username}: {message}".encode('ascii'))
        except Exception as e:
            if running:
                print(f"Client is disconnecting. {e}")
            running = False
            break

# Start the threads
receive_thread = threading.Thread(target=receive, daemon=True)
receive_thread.start()

write_thread = threading.Thread(target=write, daemon=True)
write_thread.start()

# Handle keyboard interrupt (Ctrl+C)
try:
    # Wait for threads to finish
    receive_thread.join()
    write_thread.join()
except KeyboardInterrupt:
    print("Closing connection...")
    running = False
    try:
        client.close()
    except Exception as e:
        print(f"Error closing client socket: {e}")
    sys.exit()
