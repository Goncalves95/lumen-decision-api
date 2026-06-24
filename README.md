# Lumen Decision API — Open Source Financial Decision Scoring Engine

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?logo=springboot)
![License](https://img.shields.io/badge/License-MIT-blue.svg)
![Open Source](https://img.shields.io/badge/Open%20Source-%E2%9D%A4-red)

**Lumen Decision API** is a standalone, stateless Spring Boot API that calculates a 0–100 financial decision score from a financial profile. It tells you, before you make a financial decision, how healthy your finances look across credit, income, spending, account diversity, and credit history.

No database. No accounts. No tracking. Send your numbers, get a score.

## Who it's for

- Developers building personal finance apps who want a scoring engine without building one from scratch
- Fintech hobby projects and prototypes that need a credible "financial health score" feature
- Anyone who wants to understand how their financial habits map to a score, via a simple API call

This API powers the scoring logic behind [Lumen Finance](https://github.com/Goncalves95), extracted as an independent, open source service anyone can run and use.

## Quick Start

```bash
curl -X POST http://localhost:8082/api/v1/score \
  -H "Content-Type: application/json" \
  -d '{
    "creditScore": 720,
    "creditLimit": 15000,
    "creditUsed": 3000,
    "monthlyIncome": 5000,
    "monthlyExpenses": 3200,
    "hasSavingsAccount": true,
    "hasInvestmentAccount": false,
    "accountsCount": 3,
    "avgAccountAgeYears": 4.5,
    "paymentHistoryPercent": 95.0,
    "currency": "CHF"
  }'
```

Every field is optional — call it with `{}` and sensible defaults are used.

## API Documentation

Full interactive Swagger UI is available once the app is running:

📖 **http://localhost:8082/docs**

Raw OpenAPI spec: **http://localhost:8082/api-docs**

### Endpoints

| Method | Path                  | Description                                |
|--------|------------------------|---------------------------------------------|
| POST   | `/api/v1/score`        | Calculate a decision score from a profile   |
| GET    | `/api/v1/score/example`| Sample request/response pair                |
| GET    | `/api/v1/health`       | Health check                                |

`POST /api/v1/score` is rate limited to **100 requests/hour per IP**, returning `429 Too Many Requests` when exceeded.

## Request / Response Examples

### Request

```json
{
  "creditScore": 720,
  "creditLimit": 15000,
  "creditUsed": 3000,
  "monthlyIncome": 5000,
  "monthlyExpenses": 3200,
  "hasSavingsAccount": true,
  "hasInvestmentAccount": false,
  "accountsCount": 3,
  "avgAccountAgeYears": 4.5,
  "paymentHistoryPercent": 95.0,
  "currency": "CHF"
}
```

### Response

```json
{
  "success": true,
  "data": {
    "overallScore": 78,
    "grade": "B",
    "summary": "Good financial health with room for improvement in key areas.",
    "components": [
      {
        "name": "Credit Health",
        "score": 84,
        "weight": 0.25,
        "weightedScore": 21.0,
        "description": "Reflects credit score, utilization and payment history"
      },
      {
        "name": "Income Stability",
        "score": 54,
        "weight": 0.25,
        "weightedScore": 13.5,
        "description": "Reflects how much income remains after monthly expenses"
      }
    ],
    "recommendations": [
      "Increase monthly savings rate to at least 20% of income",
      "Diversify with additional account types (savings, investment)",
      "Avoid closing old accounts — account age strengthens your profile"
    ],
    "calculatedAt": "2026-06-24T10:00:00Z",
    "currency": "CHF"
  },
  "message": null,
  "timestamp": "2026-06-24T10:00:00Z"
}
```

## Algorithm

The overall score is a weighted sum of five components, each scored 0–100:

| Component              | Weight | What it measures |
|-------------------------|--------|-------------------|
| **Credit Health**       | 25%    | Credit score, credit utilization, and payment history combined |
| **Income Stability**    | 25%    | How much of your income remains after expenses (savings rate) |
| **Spending Discipline** | 20%    | Ratio of monthly expenses to monthly income |
| **Account Diversity**   | 15%    | Number and variety of accounts held (savings, investment) |
| **Credit Age & History**| 15%    | Average account age combined with payment history |

The five weighted scores are summed into an `overallScore` (0–100), which maps to a letter grade:

| Grade | Range  | Meaning |
|-------|--------|---------|
| A     | 80–100 | Excellent financial health |
| B     | 65–79  | Good financial health |
| C     | 50–64  | Fair financial health |
| D     | 35–49  | Below average financial health |
| F     | 0–34   | Poor financial health |

The API also returns up to three actionable `recommendations`, generated from the weakest parts of the profile (e.g. high credit utilization, low savings rate, short account history).

## Running Locally

**Prerequisites:** Java 21+, Maven 3.9+

```bash
git clone https://github.com/Goncalves95/lumen-decision-api.git
cd lumen-decision-api
mvn clean install
mvn spring-boot:run
```

The API starts on **http://localhost:8082**. Visit `/docs` for Swagger UI.

## Contributing

Contributions are welcome!

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-improvement`)
3. Make your changes, with tests where applicable
4. Run `mvn clean compile` and `mvn test` to confirm everything builds and passes
5. Open a pull request describing the change and why it's useful

Ideas for contributions: additional score components, configurable weights, alternate scoring profiles (e.g. business vs. personal), client SDKs.

## Built By

Built by [Raigon Lab](https://github.com/Goncalves95) — open source tools for personal finance.

## License

This project is licensed under the **MIT License** — see below for details.

```
MIT License

Copyright (c) 2026 Raigon Lab

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
