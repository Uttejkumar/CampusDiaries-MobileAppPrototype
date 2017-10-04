from flask import Flask, request, json, jsonify #import flask framework
from sqlalchemy import create_engine #Creating a engine to load database
from sqlalchemy.orm import sessionmaker #Creating a session to access data
from database_setup import Base, User, Posts, Clubs #Importing database
from passlib.hash import sha256_crypt #password encryption

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
    return jsonify(AllPosts=[i.serialize for i in posts])

#initiating server
if __name__ == '__main__':
    app.debug = True
    app.run(host='0.0.0.0', port=8080)
