import urllib.request as urllib2

def getWeather(city):

    #create google weather api url
    url = "http://www.google.com/ig/api?weather=" + urllib2.quote(city)

    try:
        # open google weather api url
        f = urllib2.urlopen(url)
    except:
        # if there was an error opening the url, return
        return "Error opening url"

    # read contents to a string
    s = f.read()

    # extract weather condition data from xml string
    weather = s.split("<current_conditions><condition data=\"")[-1].split("\"")[0]

    # if there was an error getting the condition, the city is invalid
    if weather == "<?xml version=":
        return "Invalid city"

    #return the weather condition
    return weather

def main():
    city = "Moscow"
    weather = getWeather(city)
    print(weather)

if __name__ == "__main__":
    main()