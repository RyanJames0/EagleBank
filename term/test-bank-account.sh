#!/bin/bash

echo "=== Testing Bank Account Creation ==="
echo

BASE_URL="http://localhost:8080"

# First, create a user and get a token
echo "1. Creating user and getting token..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/v1/users" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Bank Test User",
    "email": "banktest@example.com",
    "password": "testpassword123",
    "phoneNumber": "+1234567890",
    "address": {
      "line1": "123 Bank St",
      "town": "Bank City",
      "county": "Bank County",
      "postcode": "12345"
    }
  }')

echo "User created: $LOGIN_RESPONSE"
echo

echo "2. Logging in to get token..."
TOKEN_RESPONSE=$(curl -s -X POST "$BASE_URL/v1/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "banktest@example.com",
    "password": "testpassword123"
  }')

echo "Login response: $TOKEN_RESPONSE"

# Extract token from response
TOKEN=$(echo "$TOKEN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Extracted token: $TOKEN"
echo

echo "3. Testing bank account creation..."
ACCOUNT_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$BASE_URL/v1/accounts" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Test Savings Account",
    "accountType": "SAVINGS"
  }')

echo "Account creation response: $ACCOUNT_RESPONSE"
echo

echo "=== Bank Account Test Complete ==="