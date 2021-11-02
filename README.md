# Currency Convertor
Simple REST service to convert money between currencies

## API
Convertion is available at home view ("/")
### Parameters
- **source_currency**: currency code of input money amount, e.g. EUR
- **target_currency**: code of desired currency, e.g. USD
- **amount**: money amount, e.g. 99.999
### Request example
`http://<hostname>/?source_currency=EUR&target_currency=USD&amount=99.999`
### Response example
```
{
    "status": "success",
    "currency": "USD",
    "amount": 120
}
```
