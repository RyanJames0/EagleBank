#!/bin/bash

echo "=== Testing Security Fixes ==="
echo

# Wait for server to be ready
echo "Waiting for server to start..."
sleep 5

BASE_URL="http://localhost:8080"

echo "1. Testing user creation with password..."
CREATE_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$BASE_URL/v1/users" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "securepassword123",
    "phoneNumber": "+1234567890",
    "address": {
      "line1": "123 Test St",
      "town": "Test City",
      "county": "Test County",
      "postcode": "12345"
    }
  }')

echo "$CREATE_RESPONSE"
echo

echo "2. Testing login with CORRECT password..."
LOGIN_CORRECT=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$BASE_URL/v1/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "securepassword123"
  }')

echo "$LOGIN_CORRECT"
echo

echo "3. Testing login with WRONG password (should fail)..."
LOGIN_WRONG=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$BASE_URL/v1/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "wrongpassword"
  }')

echo "$LOGIN_WRONG"
echo

echo "4. Testing login without password (should fail)..."
LOGIN_NO_PASSWORD=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$BASE_URL/v1/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com"
  }')

echo "$LOGIN_NO_PASSWORD"
echo

echo "=== Security Test Complete ==="