# Eagle Bank API Testing Scripts

This directory contains shell scripts for testing the Eagle Bank API endpoints.

## Scripts Overview

### Main Testing Scripts

**`openapi-complete-tests.sh`** (567 lines)
- Comprehensive testing of all API endpoints
- Covers success and error scenarios for every operation
- Includes authentication flow testing
- Detailed comments and organization
- Perfect for complete API validation

**`assignment-tests.sh`** (350+ lines)
- Assignment-focused test scenarios
- Uses Given/When/Then format for clarity
- Quick validation tests for core functionality
- Mapped to assignment PDF requirements

### Validation Scripts

**`test-security-fix.sh`**
- Tests password authentication security
- Validates BCrypt password hashing
- Confirms login security fixes

**`test-bank-account.sh`**
- Tests bank account creation functionality
- Validates account number generation
- Confirms database integration

**`test-enum-validation.sh`**
- Tests enum validation for account types
- Validates proper 400 error responses for invalid enums
- Tests case sensitivity and missing field validation

## Usage

1. **Start the API server:**
   ```bash
   cd .. && ./gradlew bootRun
   ```

2. **Run any test script:**
   ```bash
   cd term
   ./openapi-complete-tests.sh
   ./assignment-tests.sh
   ./test-security-fix.sh
   ```

## Prerequisites

- Eagle Bank API running on `http://localhost:8080`
- `curl` command available
- Bash shell environment

## Test Results

All scripts output HTTP status codes and response bodies for easy validation:
- ✅ 200/201: Success responses
- ❌ 400: Validation errors
- ❌ 401: Authentication errors
- ❌ 403: Authorization errors
- ❌ 404: Not found errors
- ❌ 500: Server errors

## Security Features Tested

- BCrypt password hashing
- JWT token authentication
- Input validation
- Enum validation
- Error handling