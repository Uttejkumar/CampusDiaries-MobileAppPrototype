from flask import Flask, request, json, jsonify #import flask framework
from sqlalchemy import create_engine #Creating a engine to load database
from sqlalchemy.orm import sessionmaker #Creating a session to access data
from database_setup import Base, User, Posts, Clubs #Importing database
from passlib.hash import sha256_crypt #password encryption
import base64 #imageconversion

app = Flask(__name__)

#Loading database
engine = create_engine('sqlite:///campusdiaries.db')
Base.metadata.bind = engine

#Initialising datasession
DBSession = sessionmaker(bind=engine)
dbsession = DBSession()

@app.route('/login/<string:rollno>/<string:pwd>', methods = ['GET','POST'])
def loginvalidation(rollno, pwd):
    if request.method == 'GET':
        return "Debug mode"
        pass
    if request.method == 'POST':
        loginrequester = dbsession.query(User).filter_by(rollnumber = int(rollno)).first()
        if loginrequester == None:
            return 'False'
        if sha256_crypt.verify(pwd, loginrequester.password):
            return 'True'
        else:
            return 'False'

@app.route('/register/<string:username>/<string:rollnumber>/<string:password>/<string:passwordre>', methods = ['GET','POST'])
def registration(username, rollnumber, password, passwordre):
    if request.method == 'GET':
        pass
    if request.method == 'POST':
        if password == passwordre:
            checkinguser = User()
            checkinguser = dbsession.query(User).filter_by(rollnumber = int(rollnumber)).first()
            if checkinguser == None:
                newuser = User(
                            uname = username,
                            rollnumber = rollnumber,
                            password = sha256_crypt.encrypt((str(password))),
                            email = "garbade@gmail.com",
                            dob = "0000-00-00"
                            )
                dbsession.add(newuser)
                dbsession.commit()
                return 'Success'
            else:
                return 'Exists'
        else:
            return 'Unmatched'
    if request.method == 'GET':
        pass
    return 'Error'

@app.route('/retrieveposts')
def retrieveposts():
    posts = dbsession.query(Posts).filter_by(modstatus = 1).all()
    posts = reversed(posts)
    return jsonify(AllPosts=[i.serialize for i in posts])

@app.route('/imageupload', methods = ['GET','POST'])
def imageupload():
    if request.method == 'GET':
        return 'success'  
    
    if request.method == 'POST':
        
        name = request.args.getlist('imagename')
        encodedimg = request.args.getlist('imagebits')
        postedby = request.args.getlist('postinguser')
        posttitle = request.args.getlist('title')

        imgdata = base64.b64decode(encodedimg[0])
        filename = str(name[0]) + '.png'
        path = "D:\Workspaces\Campus Diaries\static\images\postpics" + '\\' + str(filename)

        with open(path, 'wb') as f:
            f.write(imgdata)
            f.close()

        thispost = dbsession.query(Posts).filter_by(postedby = str(postedby[0]),title = str(posttitle[0])).first()
        thispost.postpic = filename
        dbsession.commit()
    
        return 'success'
    return 'fail'

@app.route('/newpost', methods = ['GET','POST'])
def newpost():
    if request.method == 'GET':
        return 'Failed'

    if request.method == 'POST':

        postinguser = request.args.getlist('postinguser')
        title = request.args.getlist('title')
        shortdesc = request.args.getlist('shortdesc')
        longdesc = request.args.getlist('longdesc')
        contact = request.args.getlist('contact')
        startdate = request.args.getlist('startdate')
        enddate = request.args.getlist('enddate')
        
        newpost = Posts(
                postedby = postinguser[0],
                club     = "",
                title    = title[0],
                shortdesc= shortdesc[0],
                longdesc = longdesc[0],
                startdate= startdate[0],
                enddate  = enddate[0],
                postpic  = "",
                contact  = contact[0]
            )

        dbsession.add(newpost)
        dbsession.commit()

        return 'Successful upload'
    return 'Failed'
        
        
        
            


#initiating server
if __name__ == '__main__':
    app.debug = True
    app.run(host='0.0.0.0', port=8080)
