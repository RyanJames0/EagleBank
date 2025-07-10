#!/bin/bash

echo "=== Testing Enum Validation ==="
echo

BASE_URL="http://localhost:8080"

# First, create a user and get a token
echo "1. Creating user and getting token..."
USER_RESPONSE=$(curl -s -X POST "$BASE_URL/v1/users" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Enum Test User",
    "email": "enumtest@example.com",
    "password": "testpassword123",
    "phoneNumber": "+1234567890",
    "address": {
      "line1": "123 Enum St",
      "town": "Enum City",
      "county": "Enum County",
      "postcode": "12345"
    }
  }')

echo "User created: $USER_RESPONSE"
echo

echo "2. Logging in to get token..."
TOKEN_RESPONSE=$(curl -s -X POST "$BASE_URL/v1/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "enumtest@example.com",
    "password": "testpassword123"
  }')

echo "Login response: $TOKEN_RESPONSE"

# Extract token from response
TOKEN=$(echo "$TOKEN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Extracted token: $TOKEN"
echo

echo "3. Testing with VALID enum value (SAVINGS)..."
VALID_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$BASE_URL/v1/accounts" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Valid Savings Account",
    "accountType": "SAVINGS"
  }')

echo "Valid enum response: $VALID_RESPONSE"
echo

echo "4. Testing with INVALID enum value (INVALID_TYPE)..."
INVALID_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$BASE_URL/v1/accounts" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Invalid Account Type",
    "accountType": "INVALID_TYPE"
  }')

echo "Invalid enum response: $INVALID_RESPONSE"
echo

echo "5. Testing with lowercase enum value (savings)..."
LOWERCASE_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$BASE_URL/v1/accounts" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Lowercase Account Type",
    "accountType": "savings"
  }')

echo "Lowercase enum response: $LOWERCASE_RESPONSE"
echo

echo "6. Testing with missing accountType field..."
MISSING_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$BASE_URL/v1/accounts" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Missing Account Type"
  }')

echo "Missing enum response: $MISSING_RESPONSE"
echo

echo "=== Enum Validation Test Complete ==="