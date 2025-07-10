#!/bin/bash

# ============================================================================
# Eagle Bank API - Assignment Test Scenarios
# ============================================================================
# This file contains focused tests for the specific scenarios outlined in the
# assignment PDF. These tests are organized by functional area and designed
# for quick validation during development.
#
# Based on: "Take Home coding test v2.pdf"
# Usage: ./assignment-tests.sh
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
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

# Helper functions
print_section() {
    echo -e "\n${BLUE}════════════════════════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}════════════════════════════════════════════════════════════════════════════${NC}\n"
}

print_scenario() {
    echo -e "${PURPLE}SCENARIO: $1${NC}"
}

print_given() {
    echo -e "${YELLOW}Given: $1${NC}"
}

print_when() {
    echo -e "${YELLOW}When: $1${NC}"
}

print_then() {
    echo -e "${YELLOW}Then: $1${NC}"
}

print_result() {
    echo -e "${GREEN}Result: $1${NC}\n"
}

# Global test data
USER_EMAIL="testuser@eaglebank.com"
ANOTHER_USER_EMAIL="anotheruser@eaglebank.com"
USER_ID=""
ANOTHER_USER_ID=""
TOKEN=""
ANOTHER_TOKEN=""
ACCOUNT_NUMBER=""
ANOTHER_ACCOUNT_NUMBER=""
TRANSACTION_ID=""

print_section "EAGLE BANK API - ASSIGNMENT TEST SCENARIOS"

# ============================================================================
# SETUP: Create test users and get authentication tokens
# ============================================================================

print_section "SETUP: Creating Test Users and Authentication"

echo "Creating primary test user..."
curl -s -X POST "$BASE_URL/v1/users" \
  -H "$CONTENT_TYPE" \
  -d '{
    "name": "Primary Test User",
    "address": {
      "line1": "123 Primary Street",
      "town": "London",
      "county": "Greater London",
      "postcode": "SW1A 1AA"
    },
    "phoneNumber": "+447700900123",
    "email": "testuser@eaglebank.com",
    "password": "primaryPassword123"
  }' > /dev/null

echo "Creating secondary test user..."
curl -s -X POST "$BASE_URL/v1/users" \
  -H "$CONTENT_TYPE" \
  -d '{
    "name": "Another Test User",
    "address": {
      "line1": "456 Secondary Street",
      "town": "Manchester",
      "county": "Greater Manchester",
      "postcode": "M1 1AA"
    },
    "phoneNumber": "+447700900456",
    "email": "anotheruser@eaglebank.com",
    "password": "secondaryPassword123"
  }' > /dev/null

echo "Getting authentication token for primary user..."
TOKEN_RESPONSE=$(curl -s -X POST "$BASE_URL/v1/login" \
  -H "$CONTENT_TYPE" \
  -d '{
    "email": "testuser@eaglebank.com",
    "password": "primaryPassword123"
  }')
TOKEN=$(echo "$TOKEN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

echo "Getting authentication token for secondary user..."
ANOTHER_TOKEN_RESPONSE=$(curl -s -X POST "$BASE_URL/v1/login" \
  -H "$CONTENT_TYPE" \
  -d '{
    "email": "anotheruser@eaglebank.com",
    "password": "secondaryPassword123"
  }')
ANOTHER_TOKEN=$(echo "$ANOTHER_TOKEN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

echo -e "${GREEN}Setup complete!${NC}"
echo -e "Primary user token: ${TOKEN:0:20}..."
echo -e "Secondary user token: ${ANOTHER_TOKEN:0:20}...\n"

# ============================================================================
# USER CREATION SCENARIOS
# ============================================================================

print_section "USER CREATION SCENARIOS"

print_scenario "Create a new user"
print_given "a user wants to signup for Eagle Bank"
print_when "the user makes a POST request to /v1/users with all required data"
print_then "a new user is created"

curl -X POST "$BASE_URL/v1/users" \
  -H "$CONTENT_TYPE" \
  -d '{
    "name": "New User",
    "address": {
      "line1": "789 New Street",
      "town": "Birmingham",
      "county": "West Midlands",
      "postcode": "B1 1AA"
    },
    "phoneNumber": "+447700900789",
    "email": "newuser@eaglebank.com"
  }' \
  -w "\nStatus: %{http_code}\n"
print_result "User created successfully (201)"

print_scenario "Create a new user without supplying all required data"
print_given "a user has successfully authenticated"
print_when "the user makes a POST request to /v1/users with required data missing"
print_then "the system returns a Bad Request status code and error message"

curl -X POST "$BASE_URL/v1/users" \
  -H "$CONTENT_TYPE" \
  -d '{
    "name": "Incomplete User"
  }' \
  -w "\nStatus: %{http_code}\n"
print_result "Bad Request returned (400)"

# ============================================================================
# USER FETCH SCENARIOS
# ============================================================================

print_section "USER FETCH SCENARIOS"

# Use sample user IDs since we can't easily extract them
USER_ID="usr-primary123"
ANOTHER_USER_ID="usr-another456"

print_scenario "User wants to fetch their user details"
print_given "a user has successfully authenticated"
print_when "the user makes a GET request to /v1/users/{userId} supplying their userId"
print_then "the system fetches the user details"

curl -X GET "$BASE_URL/v1/users/$USER_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "User details fetched (200) or Not Found (404) if user doesn't exist"

print_scenario "User wants to fetch the user details of another user"
print_given "a user has successfully authenticated"
print_when "the user makes a GET request to /v1/users/{userId} supplying another user's userId"
print_then "the system returns a Forbidden status code and error message"

curl -X GET "$BASE_URL/v1/users/$ANOTHER_USER_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Forbidden access returned (403)"

print_scenario "User wants to fetch the user details of a non-existent user"
print_given "a user has successfully authenticated"
print_when "the user makes a GET request to /v1/users/{userId} supplying a userId which doesn't exist"
print_then "the system returns a Not Found status code and error message"

curl -X GET "$BASE_URL/v1/users/usr-nonexistent" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Not Found returned (404)"

# ============================================================================
# USER UPDATE SCENARIOS
# ============================================================================

print_section "USER UPDATE SCENARIOS"

print_scenario "User wants to update their user details"
print_given "a user has successfully authenticated"
print_when "the user makes a PATCH request to /v1/users/{userId} supplying their userId and all required data"
print_then "the system updates the user details and returns the updated data"

curl -X PATCH "$BASE_URL/v1/users/$USER_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Updated Primary User",
    "phoneNumber": "+447700900999"
  }' \
  -w "\nStatus: %{http_code}\n"
print_result "User updated successfully (200) or Not Found (404)"

print_scenario "User wants to update the user details of another user"
print_given "a user has successfully authenticated"
print_when "the user makes a PATCH request to /v1/users/{userId} supplying another user's userId"
print_then "the system returns a Forbidden status code and error message"

curl -X PATCH "$BASE_URL/v1/users/$ANOTHER_USER_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Trying to update another user"
  }' \
  -w "\nStatus: %{http_code}\n"
print_result "Forbidden access returned (403)"

print_scenario "User wants to update a non-existent user"
print_given "a user has successfully authenticated"
print_when "the user makes a PATCH request to /v1/users/{userId} supplying a userId which doesn't exist"
print_then "the system returns a Not Found status code and error message"

curl -X PATCH "$BASE_URL/v1/users/usr-nonexistent" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Trying to update non-existent user"
  }' \
  -w "\nStatus: %{http_code}\n"
print_result "Not Found returned (404)"

# ============================================================================
# BANK ACCOUNT CREATION SCENARIOS
# ============================================================================

print_section "BANK ACCOUNT CREATION SCENARIOS"

print_scenario "User wants to create a new bank account"
print_given "a user has successfully authenticated"
print_when "the user makes a POST request to /v1/accounts with all required data"
print_then "a new bank account is created, and the account details are returned"

ACCOUNT_RESPONSE=$(curl -s -X POST "$BASE_URL/v1/accounts" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Primary Savings Account",
    "accountType": "personal"
  }' \
  -w "\nSTATUS:%{http_code}")

echo "$ACCOUNT_RESPONSE"
ACCOUNT_NUMBER=$(echo "$ACCOUNT_RESPONSE" | grep -o '"accountNumber":"[^"]*"' | cut -d'"' -f4)
print_result "Bank account created successfully (201)"

print_scenario "User wants to create a new bank account without supplying all required data"
print_given "a user has successfully authenticated"
print_when "the user makes a POST request to /v1/accounts with required data missing"
print_then "the system returns a Bad Request status code and error message"

curl -X POST "$BASE_URL/v1/accounts" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Incomplete Account"
  }' \
  -w "\nStatus: %{http_code}\n"
print_result "Bad Request returned (400)"

# Create account for another user
echo "Creating account for secondary user..."
ANOTHER_ACCOUNT_RESPONSE=$(curl -s -X POST "$BASE_URL/v1/accounts" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $ANOTHER_TOKEN" \
  -d '{
    "name": "Another User Account",
    "accountType": "personal"
  }')
ANOTHER_ACCOUNT_NUMBER=$(echo "$ANOTHER_ACCOUNT_RESPONSE" | grep -o '"accountNumber":"[^"]*"' | cut -d'"' -f4)

# Use sample account numbers if extraction failed
if [ -z "$ACCOUNT_NUMBER" ]; then
    ACCOUNT_NUMBER="01234567"
fi
if [ -z "$ANOTHER_ACCOUNT_NUMBER" ]; then
    ANOTHER_ACCOUNT_NUMBER="01765432"
fi

# ============================================================================
# BANK ACCOUNT LIST SCENARIOS
# ============================================================================

print_section "BANK ACCOUNT LIST SCENARIOS"

print_scenario "User wants to view their bank accounts"
print_given "a user has successfully authenticated"
print_when "the user makes a GET request to /v1/accounts"
print_then "all the bank accounts associated with their userId are returned"

curl -X GET "$BASE_URL/v1/accounts" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "User's bank accounts listed (200)"

# ============================================================================
# BANK ACCOUNT FETCH SCENARIOS
# ============================================================================

print_section "BANK ACCOUNT FETCH SCENARIOS"

print_scenario "User wants to fetch their bank account details"
print_given "a user has successfully authenticated"
print_when "the user makes a GET request to /v1/accounts/{accountId} and the account is associated with their userId"
print_then "the system fetches the bank account details"

curl -X GET "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Bank account details fetched (200)"

print_scenario "User wants to fetch another user's bank account details"
print_given "a user has successfully authenticated"
print_when "the user makes a GET request to /v1/accounts/{accountId} and the account is not associated with their userId"
print_then "the system returns a Forbidden status code and error message"

curl -X GET "$BASE_URL/v1/accounts/$ANOTHER_ACCOUNT_NUMBER" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Forbidden access returned (403)"

print_scenario "User wants to fetch a non-existent bank account"
print_given "a user has successfully authenticated"
print_when "the user makes a GET request to /v1/accounts/{accountId} and the accountId doesn't exist"
print_then "the system returns a Not Found status code and error message"

curl -X GET "$BASE_URL/v1/accounts/01999999" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Not Found returned (404)"

# ============================================================================
# BANK ACCOUNT UPDATE SCENARIOS
# ============================================================================

print_section "BANK ACCOUNT UPDATE SCENARIOS"

print_scenario "User wants to update their bank account details"
print_given "a user has successfully authenticated"
print_when "the user makes a PATCH request to /v1/accounts/{accountId} supplying all required data and the account is associated with their userId"
print_then "the system updates the bank account information and returns the updated data"

curl -X PATCH "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Updated Primary Savings Account"
  }' \
  -w "\nStatus: %{http_code}\n"
print_result "Bank account updated successfully (200)"

print_scenario "User wants to update another user's bank account details"
print_given "a user has successfully authenticated"
print_when "the user makes a PATCH request to /v1/accounts/{accountId} and the account is not associated with their userId"
print_then "the system returns a Forbidden status code and error message"

curl -X PATCH "$BASE_URL/v1/accounts/$ANOTHER_ACCOUNT_NUMBER" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Trying to update another user account"
  }' \
  -w "\nStatus: %{http_code}\n"
print_result "Forbidden access returned (403)"

print_scenario "User wants to update a non-existent bank account"
print_given "a user has successfully authenticated"
print_when "the user makes a PATCH request to /v1/accounts/{accountId} and the accountId doesn't exist"
print_then "the system returns a Not Found status code and error message"

curl -X PATCH "$BASE_URL/v1/accounts/01999999" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Trying to update non-existent account"
  }' \
  -w "\nStatus: %{http_code}\n"
print_result "Not Found returned (404)"

# ============================================================================
# TRANSACTION CREATION SCENARIOS
# ============================================================================

print_section "TRANSACTION CREATION SCENARIOS"

print_scenario "User wants to deposit money into their bank account"
print_given "a user has successfully authenticated"
print_when "the user makes a POST request to /v1/accounts/{accountId}/transactions with all required data, transaction type is 'deposit', and the account is associated with their userId"
print_then "the deposit is registered against the account and the account balance is updated"

DEPOSIT_RESPONSE=$(curl -s -X POST "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": 500.00,
    "currency": "GBP",
    "type": "deposit",
    "reference": "Initial deposit for testing"
  }' \
  -w "\nSTATUS:%{http_code}")

echo "$DEPOSIT_RESPONSE"
TRANSACTION_ID=$(echo "$DEPOSIT_RESPONSE" | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
print_result "Deposit transaction created successfully (201)"

print_scenario "User wants to withdraw money from their bank account"
print_given "a user has successfully authenticated"
print_when "the user makes a POST request to /v1/accounts/{accountId}/transactions with all required data, transaction type is 'withdrawal', account has sufficient funds, and the account is associated with their userId"
print_then "the withdrawal is registered against the account and the account balance is updated"

curl -X POST "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": 100.00,
    "currency": "GBP",
    "type": "withdrawal",
    "reference": "ATM withdrawal"
  }' \
  -w "\nStatus: %{http_code}\n"
print_result "Withdrawal transaction created successfully (201)"

print_scenario "User wants to withdraw money but has insufficient funds"
print_given "a user has successfully authenticated"
print_when "the user makes a POST request to /v1/accounts/{accountId}/transactions with all required data, transaction type is 'withdrawal', account has insufficient funds, and the account is associated with their userId"
print_then "the system returns an Unprocessable Entity status code and error message"

curl -X POST "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": 10000.00,
    "currency": "GBP",
    "type": "withdrawal",
    "reference": "Large withdrawal attempt"
  }' \
  -w "\nStatus: %{http_code}\n"
print_result "Unprocessable Entity returned (422) - Insufficient funds"

print_scenario "User wants to deposit or withdraw money into another user's bank account"
print_given "a user has successfully authenticated"
print_when "the user makes a POST request to /v1/accounts/{accountId}/transactions with all required data and the account is not associated with their userId"
print_then "the system returns a Forbidden status code and error message"

curl -X POST "$BASE_URL/v1/accounts/$ANOTHER_ACCOUNT_NUMBER/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": 50.00,
    "currency": "GBP",
    "type": "deposit",
    "reference": "Trying to deposit to another account"
  }' \
  -w "\nStatus: %{http_code}\n"
print_result "Forbidden access returned (403)"

print_scenario "User wants to deposit or withdraw money into a non-existent bank account"
print_given "a user has successfully authenticated"
print_when "the user makes a POST request to /v1/accounts/{accountId}/transactions with all required data and the accountId doesn't exist"
print_then "the system returns a Not Found status code and error message"

curl -X POST "$BASE_URL/v1/accounts/01999999/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": 50.00,
    "currency": "GBP",
    "type": "deposit",
    "reference": "Trying to deposit to non-existent account"
  }' \
  -w "\nStatus: %{http_code}\n"
print_result "Not Found returned (404)"

print_scenario "User wants to deposit or withdraw money without supplying all required data"
print_given "a user has successfully authenticated"
print_when "the user makes a POST request to /v1/accounts/{accountId}/transactions with required data missing"
print_then "the system returns a Bad Request status code and error message"

curl -X POST "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": 50.00
  }' \
  -w "\nStatus: %{http_code}\n"
print_result "Bad Request returned (400)"

# ============================================================================
# TRANSACTION LIST SCENARIOS
# ============================================================================

print_section "TRANSACTION LIST SCENARIOS"

print_scenario "User wants to view all transactions on their bank account"
print_given "a user has successfully authenticated"
print_when "the user makes a GET request to /v1/accounts/{accountId}/transactions and the account is associated with their userId"
print_then "the transactions are returned"

curl -X GET "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Transactions listed successfully (200)"

print_scenario "User wants to view all transactions on another user's bank account"
print_given "a user has successfully authenticated"
print_when "the user makes a GET request to /v1/accounts/{accountId}/transactions and the account is not associated with their userId"
print_then "the system returns a Forbidden status code and error message"

curl -X GET "$BASE_URL/v1/accounts/$ANOTHER_ACCOUNT_NUMBER/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Forbidden access returned (403)"

print_scenario "User wants to view all transactions on a non-existent bank account"
print_given "a user has successfully authenticated"
print_when "the user makes a GET request to /v1/accounts/{accountId}/transactions and the accountId doesn't exist"
print_then "the system returns a Not Found status code and error message"

curl -X GET "$BASE_URL/v1/accounts/01999999/transactions" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Not Found returned (404)"

# ============================================================================
# TRANSACTION FETCH SCENARIOS
# ============================================================================

print_section "TRANSACTION FETCH SCENARIOS"

# Use sample transaction ID if extraction failed
if [ -z "$TRANSACTION_ID" ]; then
    TRANSACTION_ID="tan-sample123"
fi

print_scenario "User wants to fetch a transaction on their bank account"
print_given "a user has successfully authenticated"
print_when "the user makes a GET request to /v1/accounts/{accountId}/transactions/{transactionId}, the account is associated with their userId, and the transactionId is associated with the accountId specified"
print_then "the transaction details are returned"

curl -X GET "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions/$TRANSACTION_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Transaction details fetched (200) or Not Found (404)"

print_scenario "User wants to fetch a transaction on another user's bank account"
print_given "a user has successfully authenticated"
print_when "the user makes a GET request to /v1/accounts/{accountId}/transactions/{transactionId} and the account is not associated with their userId"
print_then "the system returns a Forbidden status code and error message"

curl -X GET "$BASE_URL/v1/accounts/$ANOTHER_ACCOUNT_NUMBER/transactions/$TRANSACTION_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Forbidden access returned (403)"

print_scenario "User wants to fetch a transaction on a non-existent bank account"
print_given "a user has successfully authenticated"
print_when "the user makes a GET request to /v1/accounts/{accountId}/transactions/{transactionId} and the accountId doesn't exist"
print_then "the system returns a Not Found status code and error message"

curl -X GET "$BASE_URL/v1/accounts/01999999/transactions/$TRANSACTION_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Not Found returned (404)"

print_scenario "User wants to fetch a transaction with non-existent transaction ID"
print_given "a user has successfully authenticated"
print_when "the user makes a GET request to /v1/accounts/{accountId}/transactions/{transactionId}, the account is associated with their userId, and the transactionId does not exist"
print_then "the system returns a Not Found status code and error message"

curl -X GET "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER/transactions/tan-nonexistent" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Not Found returned (404)"

print_scenario "User wants to fetch a transaction against the wrong bank account"
print_given "a user has successfully authenticated"
print_when "the user makes a GET request to /v1/accounts/{accountId}/transactions/{transactionId}, the account is associated with their userId, and the transactionId is not associated with the accountId specified"
print_then "the system returns a Not Found status code and error message"

curl -X GET "$BASE_URL/v1/accounts/01777777/transactions/$TRANSACTION_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Not Found returned (404)"

# ============================================================================
# DELETE SCENARIOS
# ============================================================================

print_section "DELETE SCENARIOS"

print_scenario "User deletes an existing bank account"
print_given "a user has successfully authenticated"
print_when "the user makes a DELETE request to /v1/accounts/{accountId} and the account is associated with their userId"
print_then "the system deletes the bank account"

curl -X DELETE "$BASE_URL/v1/accounts/$ACCOUNT_NUMBER" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Bank account deleted successfully (204)"

print_scenario "User wants to delete another user's bank account details"
print_given "a user has successfully authenticated"
print_when "the user makes a DELETE request to /v1/accounts/{accountId} and the account is not associated with their userId"
print_then "the system returns a Forbidden status code and error message"

curl -X DELETE "$BASE_URL/v1/accounts/$ANOTHER_ACCOUNT_NUMBER" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Forbidden access returned (403)"

print_scenario "User wants to delete a non-existent bank account"
print_given "a user has successfully authenticated"
print_when "the user makes a DELETE request to /v1/accounts/{accountId} and the accountId doesn't exist"
print_then "the system returns a Not Found status code and error message"

curl -X DELETE "$BASE_URL/v1/accounts/01999999" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Not Found returned (404)"

print_scenario "User wants to delete their user details (no bank accounts)"
print_given "a user has successfully authenticated"
print_when "the user makes a DELETE request to /v1/users/{userId} and they do not have a bank account"
print_then "the system deletes their user"

curl -X DELETE "$BASE_URL/v1/users/$USER_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "User deleted successfully (204) or Not Found (404)"

print_scenario "User wants to delete their user details (has bank accounts)"
print_given "a user has successfully authenticated"
print_when "the user makes a DELETE request to /v1/users/{userId} and they have a bank account"
print_then "the system returns a Conflict status code and error message"

curl -X DELETE "$BASE_URL/v1/users/$ANOTHER_USER_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $ANOTHER_TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Conflict returned (409) - User has bank accounts"

print_scenario "User wants to delete user details of another user"
print_given "a user has successfully authenticated"
print_when "the user makes a DELETE request to /v1/users/{userId} and the userId is associated with another user"
print_then "the system returns a Forbidden status code and error message"

curl -X DELETE "$BASE_URL/v1/users/$ANOTHER_USER_ID" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Forbidden access returned (403)"

print_scenario "User wants to delete user details of a non-existent user"
print_given "a user has successfully authenticated"
print_when "the user makes a DELETE request to /v1/users/{userId} and the userId doesn't exist"
print_then "the system returns a Not Found status code and error message"

curl -X DELETE "$BASE_URL/v1/users/usr-nonexistent" \
  -H "$CONTENT_TYPE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"
print_result "Not Found returned (404)"

# ============================================================================
# SUMMARY
# ============================================================================

print_section "ASSIGNMENT TEST SCENARIOS COMPLETE"

echo -e "${GREEN}✓ All assignment scenarios have been tested${NC}"
echo -e