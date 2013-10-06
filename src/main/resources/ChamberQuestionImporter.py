#!bin/bash/python3

import json
from lxml import html
import os
import re

import psycopg2
import urllib3

"""
Maximum import of questions
"""
maxQuestionImport = 400

def find_date(string):
    matchObj = re.match(r'[0-9]{2}/[0-9]{2}/[0-9]{4}', string)
    r = matchObj.group()
    a = r.split('/')
    year = a[2]
    month = a[1]
    day = a[0]
    return year + "-" + month + "-" + day

def find_year(string):
    matchObj = re.match(r'[0-9]{2}/[0-9]{2}/[0-9]{4}', string)
    r = matchObj.group()
    a = r.split('/')
    year = a[2]
    month = a[1]
    day = a[0]
    return year

def clean_html(vvv):
    question = vvv.replace("'", "''")
    question = question.strip()
    question = question.strip(os.linesep)
    question = question.strip()
    question = html.document_fromstring(question).text_content()
    question = "<br />".join(question.split("\n"))
    return question






con = None

try:
    con = psycopg2.connect(database='noselus', 
                           user="noselus2", 
                           host="hackathon01.cblue.be",
                           password="noselus")
    cur = con.cursor()
    cur.execute('SELECT version()')          
    ver = cur.fetchone()
    print(ver)    
    
    rootUrl = "http://www.dierentheater.be"
    next = rootUrl + "/api/v1/written-question/?format=json&limit=10"
    i=0
    
    http = urllib3.PoolManager()
    jsonDecoder = json.JSONDecoder()
    
    queryInsertQuest = "INSERT INTO written_question (date_asked, title, question_text, answer_text, asked_by, asked_to, assembly_ref, assembly_id, answered_by, year) VALUES ('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}', '{7}', '{8}', '{9}') returning id"
    
    while next: 
        response = http.request('GET', next)
        
    
        
        data = json.loads(response.data.decode("UTF-8"))
        
        cur = con.cursor()
        
        for k,v in data.items():
            if k == 'meta':

                    for kk, vv in v.items():
                        
                        if kk == 'next':
                            next = vv
                            if next == None:
                                print("this is last call")
                            else: 
                                print("new meta is " + next)
                
            
            
            if k == 'objects':
                for item in v:
                    print("next ____________________________")
                    for kk, vv in item.items():
                        print(kk)
                        if kk == "question":
                            for kkk,vvv in vv.items():
                                if kkk == "fr":
                                    question = clean_html(vvv)
                             
                               
                        if kk == "deposition_date":
                                date = find_date(vv)
                                year = find_year(vv)
                                print(date)
                                
                        if kk == "title":
                            for kkk,vvv in vv.items():
                                if kkk == "fr":
                                    title = vvv.strip()
                                    print(title)
                                    
                        if kk == "answer":
                            for kkk,vvv in vv.items():
                                if kkk == "fr":
                                    answer = clean_html(vvv)
                                    print(answer)
                        
                        if kk == "author":
                            author = vv.strip()
                            author = " ".join(author.split("\n"))
                            author_info = author.split(",")
                            author_name = author_info[0].strip()
                            author_party = author_info[1].strip()
                            #remove whitespaces
                            author_name = re.sub(' +', ' ', author_name)
                            author_name_splitted = author_name.split(" ")
                            print(author_name)
                            print(author_party)
                            
                            print(author_name_splitted[0])
                            print(author_name_splitted[1])
                            author_search = con.cursor()
                            query = "SELECT id, full_name, party from person where full_name like '%{0}%' AND full_name like '%{1}%' ".format(author_name_splitted[0], author_name_splitted[1])
                                    
                            author_search.execute(query)
                            rows = author_search.fetchall()
                            
                            for row in rows:
                                print(row)
                            
                            author_id = rows[0][0]
                            
                            if rows[0][2] == None:
                                """add party to person
                                """
                                add_party = con.cursor()
                                add_party.execute("UPDATE person set party='{0}' where id = {1}"
                                                  .format(author_party.strip(), rows[0][0]))
                                #con.commit()
                                
                        if kk == "lachambre_id":
                            chambre_id = vv
                            print(chambre_id)
                            
                        if kk == "departement":
                            for kkk,vvv in vv.items():
                                if kkk == "fr":
                                    print(vvv.strip())
                                    departement_splitted = vvv.split()
                                    departement_search = con.cursor()
                                    query = "SELECT id from person where full_name like '%{0}%' and full_name like '%{1}%' and full_name like '%{2}%' ".format(
                                                                                                                                                               departement_splitted[-1].replace("'", "''''"), departement_splitted[-2].replace("'", "''''"), departement_splitted[-3].replace("'", "''''"))
                                    print(query)        
                                    departement_search.execute(query)
                                    rows = departement_search.fetchall()
                                    print("departement trouvés : ")
                                    print(rows)
                                    
                                    if len(rows) == 1:
                                        asked_to = rows[0][0]
                                    elif len(rows) == 0:
                                        query = "insert into person (full_name, function) values ('{0}', 'MINISTER') RETURNING id".format(vvv.strip().replace("'", "''''"))
                                        departement_add = con.cursor()
                                        departement_add.execute(query)
                                        id_of_new_row = departement_add.fetchone()[0]
                                        asked_to = id_of_new_row
                                        print(id_of_new_row)
                                        #con.commit()
                        
                                    
                        if kk == "eurovoc_descriptors":
                            for kkk, vvv in vv.items():
                                if kkk == "fr":
                                    eurovocs = list()
                                    for eurovoc in vvv:
                                        eurovocs.append(eurovoc.strip())
                                    print(eurovocs)
                                    
#                         if kk == "publication_date": 
#                            if len(vv) > 0:(
#                                for email in vv:
#                                    email_ = email
#                            else: 
#                                email_ = ''
#                                
                    query = queryInsertQuest.format(
                                                        date,
                                                        title.replace("'", "''''"),
                                                        question,
                                                        answer,
                                                        author_id,
                                                        asked_to,
                                                        chambre_id.replace("'", "''''"),
                                                        2, #id oaf chambre des représentants
                                                        asked_to,
                                                        year)
                    print(query)    
                    cur.execute(query)
                    last_id = cur.fetchone()[0]
                    print(last_id)
                    i = i+1
                    #con.commit()
                    print(eurovocs)
                    string = "insert into written_question_eurovoc (id_written_question, id_eurovoc) SELECT {0}, id from eurovoc where LOWER(label) like LOWER('{1}')"
                    for eurovoc in eurovocs:
                        q = string.format(last_id, eurovoc.replace("'", "''"))
                        insert_eurovoc = con.cursor()
                        insert_eurovoc.execute(q)
                        print(q)
                        
                                
                    
                    
            
            
            
            con.commit()
            
            if i > maxQuestionImport:
                next = None
        
        
        
        
        #con.rollback()
        if next == None:
            next = None
        else: 
            next = rootUrl + next
            
       
        
    
    
    
    

except (psycopg2.DatabaseError, e):
    print ('Error %s' % e)   
    sys.exit(1)
    
    
finally:
    
    if con:

        con.close()