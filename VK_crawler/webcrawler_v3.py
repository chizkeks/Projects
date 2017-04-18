import sys
import os
import urllib.parse
import urllib.request
import json
import csv
from selenium import webdriver
import webbrowser

# Ручной парсинг записей со страниц профилей и групп Вконтакте
def getWallNotes(ownerId, count, type_of_notes, offset):

    url = "https://api.vk.com/method/wall.get?owner_id=-" + ownerId + "&offset=" + str(offset) + "&count=" + str(count) + "&filter=" + type_of_notes + "&fields=members_count&v=5.62"
    data = urllib.request.urlopen(url).read()
    result = []
    print(data)
    try: js = json.loads(data.decode("utf-8"))
    except: js = None

    for item in js["response"]["items"]:
        try: ownerText = item['text']
        except: ownerText = "No text"

        try: repostText = item['copy_history']['text']
        except: repostText = "No text"

        li = [ownerText, repostText]
        result.append(li)
    return result


# "Вытаскивание" id подписчика/друга из ответа сервера
def idParsing(data):

     result = []
     try: js = json.loads(data.decode("utf-8"))
     except: js = None
     for item in js["response"]["items"]:
         idPerson = item['id']
         result.append(idPerson)

     return result


def deepGetFriends (friendsList, parsing_depth, count):
    result = []
    if parsing_depth == 2:
        for frId in friendsList:
            strFrId = str(frId)
            url1 = "https://api.vk.com/method/friends.get?user_id=" + strFrId + "&count=" + count + "&fields=city,domain&v=5.62"
            friends1 = urllib.request.urlopen(url1).read()
            print('Retrieved', len(friends1), 'characters')
            try:
                friendsList1 = idParsing(friends1)
                for frfrId in friendsList1:
                    res = [strFrId, str(frfrId)]
                    result.append(res)
            except: pass
        return result

    if parsing_depth == 3:
        for frId in friendsList:
            strFrId = str(frId)
            url1 = "https://api.vk.com/method/friends.get?user_id=" + strFrId + "&count=" + count + "&fields=city,domain&v=5.62"
            friends1 = urllib.request.urlopen(url1).read()
            print('Retrieved', len(friends1), 'characters')
            try:
                friendsList1 = idParsing(friends1)
                for frfrId in friendsList1:
                    strFrFrId = str(frfrId)
                    url2 =  "https://api.vk.com/method/friends.get?user_id=" + strFrFrId + "&count=" + count + "&fields=city,domain&v=5.62"
                    friends2 = urllib.request.urlopen(url2).read()
                    print('Retrieved', len(friends2), 'characters')
                    try:
                        friendsList1 = idParsing(friends2)
                        for frfrId3 in friendsList1:
                            res = [strFrId, str(frfrId3)]
                            result.append(res)
                    except: pass
            except: pass
        return result


# Парсинг подписчиков/друзей группы или страницы профиля
def getFriends (type_of_id, id_for_search, count, parsing_depth, offset):
    result = []

    # Для страницы профиля
    if type_of_id.lower() == "user":
        url = "https://api.vk.com/method/friends.get?user_id=" + id_for_search + "&count=" + count + "&fields=city,domain&v=5.62"
        friends = urllib.request.urlopen(url).read()
        print('Retrieved', len(friends), 'characters')
        try:
            friendsList = idParsing(friends)
            for frId in friendsList:
                res = [id_for_search, str(frId)]
                result.append(res)
            if parsing_depth > 1:
                t = deepGetFriends(friendsList, parsing_depth, count)
                result = result + t
        except: pass
        return result

    # Для подписчиков группы
    elif type_of_id.lower() == "group":

        # Поиск подписчиков группы
        url = "https://api.vk.com/method/groups.getMembers?group_id=" + id_for_search + "&count=" + count + "&offset=" + offset + "&fields=city,domain&v=5.62"
        members = urllib.request.urlopen(url).read()
        print('Retrieved', len(members), 'characters')
        try:
            friendsList = idParsing(members)
            for frId in friendsList:
                res = [id_for_search, str(frId)]
                result.append(res)
            if parsing_depth > 1:
                t = deepGetFriends(friendsList, parsing_depth, count)
                result = result + t
        except: pass
        return result


def resultWriter(data, first_row, second_row,file_name):
    with open(file_name, 'w', newline='') as csv_file:
        fieldtext = [first_row, second_row]
        #csv_writer = csv.writer(csv_file)
        writer = csv.DictWriter(csv_file, fieldnames=fieldtext, delimiter=';')
        writer.writeheader()
        for items in data:
            writer.writerow({first_row: items[0], second_row: items[1]})
            #csv_writer.writerow(item)
    csv_file.close()


def authUser ():
    url = "https://oauth.vk.com/authorize?client_id=5913645&display=popup&redirect_uri=https://oauth.vk.com/blank.html&scope=wall&response_type=token&v=5.62&state=123456"
    driver = webdriver.Edge('C:\Programs\Microsoft Web Driver\MicrosoftWebDriver.exe')
    driver.get(url)

'''
Глубина парсинга:
1 - Друзья
2 - Друзья друзей
3 - Друзья подписчиков и их друзья
'''

if __name__ == "__main__":
    result = []
    func = int(input('''
    Выберите функцию, которую хотите использовать:
    1 - getWallNotes - получить данные со стены группы/пользователя
    2 - getFriends - получить список друзей пользователя/подписчиков группы в формате id1:id2
    3 - getNewsfeed - получить посты с новостной ленты
    '''))
    if (func == 1):

        first_row_name = "owner_text"
        second_row_name = "repost_text"
        file_name = 'text.csv'
    # Переменные для ручного парсинга записей со страниц профилей и групп Вконтакте
        ownerId = input("Введите id пользователя или группы")
        parsing_depth = int(input("Введит количество записей, которое вы хотите получить (по умолчанию - все записи"))
        type_of_notes = input('''
        Выберите тип записей, которые необходимо получить:
        owner — записи владельца стены;
        others — записи не от владельца стены;
        all — все записи на стене (owner + others)
        ''')
        #ownerId = "
        # "
        #parsing_depth = 208
        #type_of_notes = "all"
        if(parsing_depth > 100):
            offset = 0
            count = 100
            cnt = int(parsing_depth/100)
            ost = parsing_depth%100
            while(cnt > 0):
                gw = getWallNotes(ownerId, count, type_of_notes, offset)
                result = result + gw
                cnt = cnt - 1
                offset = offset + 100
            gw = getWallNotes(ownerId, ost, type_of_notes, (parsing_depth - ost))
            result = result + gw
            print(len(result))
            print(ost)
        else:
            result = getWallNotes(ownerId, parsing_depth, type_of_notes, str(0))
        resultWriter(result, first_row_name,second_row_name, file_name)
    #type_of_notes("")

    elif(func == 2):

        first_row_name = "id1"
        second_row_name = "id2"
        file_name = 'friends.csv'
    # Переменный для парсинга подписчиков/друзей группы или страницы профиля
        #type_of_id = "group"
        type_of_id = input("Выберите тип id : group - группа, user - пользователь")
        #id_for_search = "79138567"
        id_for_search = input("Введите id пользователя или группы")
        #number_of_friends = 1500
        number_of_friends = int(input("Введите количество друзей/подписчиков, которое необходимо выбрать"))
        #parsing_depth = 2
        parsing_depth = int(input('''
        Введите глуюину поиска:
        1 - друзья/подписчики
        2 - друзья друзей/ друзья подписчиков
        3 - друзья друзей и их друзья / друзья подписчиков и их друзья
        '''))
        if type_of_id.lower() == "group":
            if(number_of_friends > 1000):
                offset = 0
                count = 1000
                cnt = number_of_friends/1000
                ost = number_of_friends%1000
                while(cnt > 0):
                    gw = getFriends(type_of_id, id_for_search, str(count), parsing_depth, str(offset))
                    result = result + gw
                    cnt = cnt - 1
                    offset = offset + 100
                gw = getFriends(type_of_id, id_for_search, str(number_of_friends),  parsing_depth, str(count - ost))
                result = result + gw
                print(len(result))
                print("Ostatok: " + str(ost))
            else:
                result = getFriends(type_of_id, id_for_search, str(number_of_friends),  parsing_depth, str(0))
                print(len(result))
        else:
            result = getFriends(type_of_id, id_for_search, str(number_of_friends),  parsing_depth, str(0))
            print(len(result))
        resultWriter(result, first_row_name,second_row_name, file_name)
    #result = getFriends(type_of_id, id_for_search, str(number_of_friends),  parsing_depth, str(0))
    #print(len(result))
    #gf = getFriends(type_of_id, id_for_search, count, pars_depth)
  #  first_row_name = "id1"
  #  second_row_name = "id2"
  #  resultWriter(gf)
   # print(gf)

   #authUser()
    #print(gw)

