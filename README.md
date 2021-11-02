# Currency Convertor
A simple REST service to convert money between currencies
## API
Convertion is available at root path ("/")
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
## Configuration
Currency Convertor retrieves exchange rates from external service:  
https://exchangeratesapi.io  
Its API key is necessary and must be specified by either:
- passing it as environment variable `EXCHANGE_RATES_API_KEY`
- putting it into application.properties as `rates.api.key`
