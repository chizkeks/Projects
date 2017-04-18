import telebot
import constants
import weather
import pywapi

bot = telebot.TeleBot(constants.token)

print(bot.get_me)



def log(message, answer):
    print("\n ----------------")
    from datetime import datetime
    print(datetime.now())
    print("Сообщение от (0) (1). (id = (2)) \n Текст - (3)".format(message.from_user.first_name,
                                                                   message.from_user.last_name,
                                                                   str(message.from_user.id),
                                                                   message.text))
    print(answer)





@bot.message_handler(commands=['help'])
def handle_text(message):
    bot.send_message(message.chat.id, constants.helpAnswer)
    log(message, constants.helpAnswer)

@bot.message_handler(commands=['start'])
def handle_start(message):
    user_markup = telebot.types.ReplyKeyboardMarkup(True, False)
    user_markup.row('/start', '/help')
    user_markup.row('/moscow', '/piter')
    bot.send_message(message.chat.id, constants.startAnswer, reply_markup = user_markup)
    log(message, constants.startAnswer)

@bot.message_handler(commands=['moscow'])
def handle_start(message):
    weather_com_result = pywapi.get_weather_from_weather_com('RSXX0063')
    weatherReport = "It is " + weather_com_result['current_conditions']['text'].lower() + " and " + weather_com_result['current_conditions']['temperature'] + "°C now in Moscow."
    bot.send_message(message.chat.id, weatherReport)
    log(message, constants.helpAnswer)

@bot.message_handler(commands=['piter'])
def handle_start(message):
    weather_com_result = pywapi.get_weather_from_weather_com('RSXX0091')
    weatherReport = "It is " + weather_com_result['current_conditions']['text'].lower() + " and " + weather_com_result['current_conditions']['temperature'] + "°C now in  Saint Petersburg."
    bot.send_message(message.chat.id, weatherReport)
    log(message, constants.helpAnswer)



bot.polling(none_stop=True, interval=0)