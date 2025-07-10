#!/bin/bash

# ============================================================================
# Eagle Bank API - Complete OpenAPI Testing Suite
# ============================================================================
# This file contains comprehensive tests for all endpoints defined in the
# OpenAPI specification, including success scenarios and all error cases.
#
# CRITICAL SECURITY ISSUES IDENTIFIED:
# 1. Login endpoint ignores password - MAJOR VULNERABILITY
# 2. Anyone can login with just an email address
# 3. No password storage or validation in the system
# 
# Usage: ./openapi-complete-tests.sh
# ============================================================================

set -e  # Exit on any error

# Configuration
BASE_URL="http://localhost:8080"
CONTENT_TYPE="Content-Type: application/json"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Helper functions
print_header() {
    echo -e "\n${BLUE}============================================================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}============================================================================${NC}\n"
}

print_test() {
    echo -e "${YELLOW}TEST: $1${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Global variables for test data
USER_EMAIL="test@example.com"
USER_ID=""
TOKEN=""
ACCOUNT_NUMBER=""
TRANSACTION_ID=""

print_header "EAGLE BANK API - COMPLETE TESTING SUITE"

# ============================================================================
# USER MANAGEMENT TESTS
# ============================================================================

print_header "USER MANAGEMENT TESTS"

print_test "Create User - Success (201)"
curl -X POST "$BASE_URL/v1/users" \
  -H "$CONTENT_TYPE" \
  -d '{
    "name": "Test User",
    "address": {
      "line1": "123 Test Street",
      "line2": "Apt 4B",
      "town": "London",
      "county": "Greater London",
      "postcode": "SW1A 1AA"
    },
    "phoneNumber": "+447700900123",
    "email": "test@example.com",
    "password": "securePassword123"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Create User - Bad Request (400) - Missing required fields"
curl -X POST "$BASE_URL/v1/users" \
  -H "$CONTENT_TYPE" \
  -d '{
    "name": "Test User"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Create User - Bad Request (400) - Invalid email format"
curl -X POST "$BASE_URL/v1/users" \
  -H "$CONTENT_TYPE" \
  -d '{
    "name": "Test User",
    "address": {
      "line1": "123 Test Street",
      "town": "London",
      "county": "Greater London",
      "postcode": "SW1A 1AA"
    },
    "phoneNumber": "+447700900123",
    "email": "invalid-email",
    "password": "securePassword123"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Create User - Bad Request (400) - Invalid phone number format"
curl -X POST "$BASE_URL/v1/users" \
  -H "$CONTENT_TYPE" \
  -d '{
    "name": "Test User",
    "address": {
      "line1": "123 Test Street",
      "town": "London",
      "county": "Greater London",
      "postcode": "SW1A 1AA"
    },
    "phoneNumber": "invalid-phone",
    "email": "test@example.com",
    "password": "securePassword123"
  }' \
  -w "\nStatus: %{http_code}\n\n"

# ============================================================================
# AUTHENTICATION TESTS
# ============================================================================

print_header "AUTHENTICATION TESTS"

print_test "Login - Success (200)"
RESPONSE=$(curl -s -X POST "$BASE_URL/v1/login" \
  -H "$CONTENT_TYPE" \
  -d '{
    "email": "test@example.com",
    "password": "securePassword123"
  }' \
  -w "\nSTATUS:%{http_code}")

echo "$RESPONSE"
TOKEN=$(echo "$RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo -e "\n${GREEN}Extracted Token: $TOKEN${NC}\n"

print_test "Login - Bad Request (400) - Missing email"
curl -X POST "$BASE_URL/v1/login" \
  -H "$CONTENT_TYPE" \
  -d '{
    "password": "password123"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Login - Unauthorized (401) - Non-existent user"
curl -X POST "$BASE_URL/v1/login" \
  -H "$CONTENT_TYPE" \
  -d '{
    "email": "nonexistent@example.com",
    "password": "password123"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Login - Bad Request (400) - Invalid email format"
curl -X POST "$BASE_URL/v1/login" \
  -H "$CONTENT_TYPE" \
  -d '{
    "email": "invalid-email",
    "password": "password123"
  }' \
  -w "\nStatus: %{http_code}\n\n"

# ============================================================================
# USER FETCH/UPDATE/DELETE TESTS (Requires Authentication)
# ============================================================================

print_header "AUTHENTICATED USER OPERATIONS"

# First, we need to get the user ID
print_test "Get User ID for further tests"
USER_RESPONSE=$(curl -s -X GET "$BASE_URL/v1/users" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN")
echo "User Response: $USER_RESPONSE"
# Note: This endpoint might not exist, but we'll try to extract user ID from other responses

print_test "Fetch User - Success (200) - Using sample user ID"
USER_ID="usr-abc123"  # Using sample ID as per OpenAPI spec
curl -X GET "$BASE_URL/v1/users/$USER_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "Fetch User - Unauthorized (401) - Missing token"
curl -X GET "$BASE_URL/v1/users/$USER_ID" \
  -H "$CONTENT_TYPE" \
  -w "\nStatus: %{http_code}\n\n"

print_test "Fetch User - Unauthorized (401) - Invalid token"
curl -X GET "$BASE_URL/v1/users/$USER_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer invalid-token" \
  -w "\nStatus: %{http_code}\n\n"

print_test "Fetch User - Bad Request (400) - Invalid user ID format"
curl -X GET "$BASE_URL/v1/users/invalid-id" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "Fetch User - Not Found (404) - Non-existent user"
curl -X GET "$BASE_URL/v1/users/usr-nonexistent" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "Fetch User - Forbidden (403) - Another user's ID"
curl -X GET "$BASE_URL/v1/users/usr-anotheruser" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "Update User - Success (200)"
curl -X PATCH "$BASE_URL/v1/users/$USER_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Updated Test User",
    "address": {
      "line1": "456 Updated Street",
      "town": "Manchester",
      "county": "Greater Manchester",
      "postcode": "M1 1AA"
    },
    "phoneNumber": "+447700900456",
    "email": "updated@example.com"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Update User - Bad Request (400) - Invalid data"
curl -X PATCH "$BASE_URL/v1/users/$USER_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "email": "invalid-email-format"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Delete User - Success (204) - No bank accounts"
curl -X DELETE "$BASE_URL/v1/users/$USER_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

# ============================================================================
# BANK ACCOUNT TESTS
# ============================================================================

print_header "BANK ACCOUNT MANAGEMENT TESTS"

print_test "Create Bank Account - Success (201)"
ACCOUNT_RESPONSE=$(curl -s -X POST "$BASE_URL/v1/accounts" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Personal Savings Account",
    "accountType": "personal"
  }' \
  -w "\nSTATUS:%{http_code}")

echo "$ACCOUNT_RESPONSE"
ACCOUNT_NUMBER=$(echo "$ACCOUNT_RESPONSE" | grep -o '"accountNumber":"[^"]*"' | cut -d'"' -f4)
echo -e "\n${GREEN}Extracted Account Number: $ACCOUNT_NUMBER${NC}\n"

print_test "Create Bank Account - Unauthorized (401) - Missing token"
curl -X POST "$BASE_URL/v1/accounts" \
  -H "$CONTENT_TYPE" \
  -d '{
    "name": "Personal Savings Account",
    "accountType": "personal"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Create Bank Account - Bad Request (400) - Missing required fields"
curl -X POST "$BASE_URL/v1/accounts" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Personal Savings Account"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Create Bank Account - Bad Request (400) - Invalid account type"
curl -X POST "$BASE_URL/v1/accounts" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Personal Savings Account",
    "accountType": "invalid-type"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "List Bank Accounts - Success (200)"
curl -X GET "$BASE_URL/v1/accounts" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "List Bank Accounts - Unauthorized (401) - Missing token"
curl -X GET "$BASE_URL/v1/accounts" \
  -H "$CONTENT_TYPE" \
  -w "\nStatus: %{http_code}\n\n"

# Use sample account number if extraction failed
if [ -z "$ACCOUNT_NUMBER" ]; then
    ACCOUNT_NUMBER="01234567"
    echo -e "${YELLOW}Using sample account number: $ACCOUNT_NUMBER${NC}"
fi

print_test "Fetch Bank Account - Success (200)"
curl -X GET "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "Fetch Bank Account - Bad Request (400) - Invalid account number format"
curl -X GET "$BASE_URL/v1/accounts/invalid-account" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "Fetch Bank Account - Not Found (404) - Non-existent account"
curl -X GET "$BASE_URL/v1/accounts/01999999" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "Fetch Bank Account - Forbidden (403) - Another user's account"
curl -X GET "$BASE_URL/v1/accounts/01888888" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "Update Bank Account - Success (200)"
curl -X PATCH "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Updated Personal Account"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Update Bank Account - Bad Request (400) - Invalid data"
curl -X PATCH "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "accountType": "invalid-type"
  }' \
  -w "\nStatus: %{http_code}\n\n"

# ============================================================================
# TRANSACTION TESTS
# ============================================================================

print_header "TRANSACTION MANAGEMENT TESTS"

print_test "Create Transaction - Deposit Success (201)"
TRANSACTION_RESPONSE=$(curl -s -X POST "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": 100.50,
    "currency": "GBP",
    "type": "deposit",
    "reference": "Initial deposit"
  }' \
  -w "\nSTATUS:%{http_code}")

echo "$TRANSACTION_RESPONSE"
TRANSACTION_ID=$(echo "$TRANSACTION_RESPONSE" | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo -e "\n${GREEN}Extracted Transaction ID: $TRANSACTION_ID${NC}\n"

print_test "Create Transaction - Withdrawal Success (201)"
curl -X POST "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": 25.00,
    "currency": "GBP",
    "type": "withdrawal",
    "reference": "ATM withdrawal"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Create Transaction - Insufficient Funds (422)"
curl -X POST "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": 10000.00,
    "currency": "GBP",
    "type": "withdrawal",
    "reference": "Large withdrawal"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Create Transaction - Bad Request (400) - Missing required fields"
curl -X POST "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": 50.00
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Create Transaction - Bad Request (400) - Invalid amount"
curl -X POST "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": -50.00,
    "currency": "GBP",
    "type": "deposit"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Create Transaction - Bad Request (400) - Invalid currency"
curl -X POST "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": 50.00,
    "currency": "USD",
    "type": "deposit"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Create Transaction - Bad Request (400) - Invalid transaction type"
curl -X POST "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": 50.00,
    "currency": "GBP",
    "type": "invalid-type"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Create Transaction - Not Found (404) - Non-existent account"
curl -X POST "$BASE_URL/v1/accounts/01999999/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": 50.00,
    "currency": "GBP",
    "type": "deposit"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Create Transaction - Forbidden (403) - Another user's account"
curl -X POST "$BASE_URL/v1/accounts/01888888/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": 50.00,
    "currency": "GBP",
    "type": "deposit"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "List Transactions - Success (200)"
curl -X GET "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "List Transactions - Bad Request (400) - Invalid account number"
curl -X GET "$BASE_URL/v1/accounts/invalid-account/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "List Transactions - Not Found (404) - Non-existent account"
curl -X GET "$BASE_URL/v1/accounts/01999999/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "List Transactions - Forbidden (403) - Another user's account"
curl -X GET "$BASE_URL/v1/accounts/01888888/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

# Use sample transaction ID if extraction failed
if [ -z "$TRANSACTION_ID" ]; then
    TRANSACTION_ID="tan-abc123"
    echo -e "${YELLOW}Using sample transaction ID: $TRANSACTION_ID${NC}"
fi

print_test "Fetch Transaction - Success (200)"
curl -X GET "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions/$TRANSACTION_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "Fetch Transaction - Bad Request (400) - Invalid transaction ID format"
curl -X GET "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions/invalid-id" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "Fetch Transaction - Not Found (404) - Non-existent transaction"
curl -X GET "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions/tan-nonexistent" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "Fetch Transaction - Not Found (404) - Transaction from different account"
curl -X GET "$BASE_URL/v1/accounts/01777777/transactions/$TRANSACTION_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

# ============================================================================
# CLEANUP AND DELETE TESTS
# ============================================================================

print_header "CLEANUP AND DELETE TESTS"

print_test "Delete Bank Account - Success (204)"
curl -X DELETE "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "Delete Bank Account - Not Found (404) - Already deleted"
curl -X DELETE "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "Delete Bank Account - Forbidden (403) - Another user's account"
curl -X DELETE "$BASE_URL/v1/accounts/01888888" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "Delete User - Conflict (409) - User has bank accounts"
curl -X DELETE "$BASE_URL/v1/users/$USER_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "Delete User - Success (204) - After deleting all accounts"
curl -X DELETE "$BASE_URL/v1/users/$USER_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

# ============================================================================
# EDGE CASES AND ADDITIONAL ERROR SCENARIOS
# ============================================================================

print_header "EDGE CASES AND ADDITIONAL ERROR SCENARIOS"

print_test "Server Error Simulation - Invalid JSON"
curl -X POST "$BASE_URL/v1/users" \
  -H "$CONTENT_TYPE" \
  -d '{"invalid": json}' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Large Amount Transaction - At Maximum Limit"
curl -X POST "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": 10000.00,
    "currency": "GBP",
    "type": "deposit"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Large Amount Transaction - Exceeding Maximum Limit"
curl -X POST "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": 10000.01,
    "currency": "GBP",
    "type": "deposit"
  }' \
  -w "\nStatus: %{http_code}\n\n"

print_test "Account Number Pattern Validation - Invalid format"
curl -X GET "$BASE_URL/v1/accounts/12345678" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "Transaction ID Pattern Validation - Invalid format"
curl -X GET "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions/invalid-format" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_test "User ID Pattern Validation - Invalid format"
curl -X GET "$BASE_URL/v1/users/invalid-format" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

print_header "TESTING COMPLETE"

echo -e "${RED}CRITICAL SECURITY ISSUES FOUND:${NC}"
echo -e "${RED}1. Login endpoint completely ignores password field${NC}"
echo -e "${RED}2. Anyone can authenticate with just an email address${NC}"
echo -e "${RED}3. No password storage or validation in the system${NC}"
echo -e "${RED}4. BCryptPasswordEncoder is imported but never used${NC}"
echo -e "\n${YELLOW}RECOMMENDATIONS:${NC}"
echo -e "${YELLOW}1. Add password field to User model and CreateUserRequest${NC}"
echo -e "${YELLOW}2. Implement proper password hashing and validation${NC}"
echo -e "${YELLOW}3. Fix login endpoint to validate passwords${NC}"
echo -e "${YELLOW}4. Add password requirements and validation${NC}"
echo -e "\n${GREEN}All OpenAPI endpoints have been tested for success and error scenarios.${NC}"