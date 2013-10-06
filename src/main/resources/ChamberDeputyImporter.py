#!bin/bash/python3

import psycopg2
import urllib3
import json






exit()





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
    next = rootUrl + "/api/v1/deputy/?format=json&limit=20"
    
    http = urllib3.PoolManager()
    jsonDecoder = json.JSONDecoder()
    
    query = "INSERT INTO person (full_name, email, site, function, assembly_id) VALUES ('%s', '%s', '%s', 'DEPUTY', '%s')"
    
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
                        if kk == "full_name":
                            full_name = vv.replace("'", "''''")
                            print(full_name)
                            
                        if kk == "emails": 
                            if len(vv) > 0:
                                for email in vv:
                                    email_ = email
                            else:
                                email_ = ''
                                
                        if kk == "websites":
                            if len(vv) > 0:
                                for website in vv:
                                    website_ = website
                            else:
                                website_ = '' 
                                
                                
                        if kk == "lachambre_id":
                            chamber_id = vv
                            chamber_id = chamber_id.lstrip('0')
                            print(chamber_id)
                            
                    cur.execute(query % (full_name, email_, website_, chamber_id))
                
        
        
        
        
        
        con.commit()
        
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