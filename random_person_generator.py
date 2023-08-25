import requests
import json

f = open("persons.txt", "a")

for x in range(20):
    resp = requests.get('https://randomuser.me/api/')
    response_json = resp.json()['results'][0]
    first_name = response_json['name']['first']
    last_name = response_json['name']['last']
    birth_date = response_json['dob']['date'].split('T')[0]
    f.write(f"{x + 1}" + "," + first_name + "," +
            last_name + "," + birth_date + "\n")

f.close()
