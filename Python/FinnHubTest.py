import requests
r = requests.get('https://finnhub.io/api/v1/stock/candle?symbol=AAPL&resolution=1&from=1615298999&to=1615302599&token=c1vv82l37jkoemkedus0')
print(r.json())