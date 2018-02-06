
import os, glob, sys,subprocess, zipfile
from subprocess import check_output
import zipfile
import sys
import os
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from email.mime.base import MIMEBase
from email import encoders
import re


def linecount(filename):
    return (check_output(["wc", "-l", filename]).split()[0])


filePath = "/Users/cedrichansen/IdeaProjects/csc344hw05"

mainHtmlFile = open('csc344hw05.html', 'w')
cHtml = open('CSC344hw5-c.html', 'w')
clojureHtml = open('CSC344hw5-clojure.html', 'w')
scalaHtml = open('CSC344hw5-scala.html', 'w')
prologHtml = open('CSC344hw5-prolog.html', 'w')
pythonHtml = open('CSC344hw5-python.html', 'w')

cFile= "/Users/cedrichansen/IdeaProjects/csc344hw05/main.c"
clojureFile="/Users/cedrichansen/IdeaProjects/csc344hw05/core.clj"
scalaFile="/Users/cedrichansen/IdeaProjects/csc344hw05/main.scala"
prologFile="/Users/cedrichansen/IdeaProjects/csc344hw05/csc344hw04.pl"
pythonFile="/Users/cedrichansen/IdeaProjects/csc344hw05/main.py"

cFileOpened = open("main.c")
clojureFileOpened = open("core.clj")
scalaFileOpened = open("main.scala")
prologFileOpened = open("csc344hw04.pl")
pythonFileOpened = open("main.py")

def getIdentifiers(file):
    a = set()
    removeComments = re.compile("((#.*)|\".*?\"|\'.*?\'|(//.*)|(--.*)$)|/\*.*\*/")
    validWords = re.compile("[a-zA-Z_]+\w*")
    for line in file:
        identifiers = validWords.findall(removeComments.sub("",line))
        a |= set(identifiers)
    return [os.path.basename(sym) for sym in a]


def processIdentifiersforTesting(file):
    a = getIdentifiers(file)
    for i in a:
        print i


def processForHtmlfile(file):
    f = "<br>".join(file)
    return f

#print(processForHtmlfile(getIdentifiers(cFileOpened)))

#processIdentifiersforTesting(cFileOpened)

#cProcessed = processForHtmlfile(cFileOpened)
#print(cProcessed)

"""
a = ["d","e","f"]

def test(l):
    f = "a".join(a)
    return f

print(test(a))
"""


#main page html file
mainMessage = """<html>
<head><title>CSC344</title></head>
<body><h1>CSC344hw05</h1> 
<h2>Cedric Hansen</h2>
<img src="http://cdn.shopify.com/s/files/1/1061/1924/products/Emoji_Icon_-_Cowboy_emoji_grande.png?v=1485573421" alt="" height="142">
<ul>
<li> <a href="CSC344hw5-c.html">Assigment 1 (C)</a></li>
<li> <a href="CSC344hw5-clojure.html">Assigment 2 (Clojure) </a></li>
<li> <a href="CSC344hw5-scala.html">Assigment 3 (Scala) </a></li>
<li> <a href="CSC344hw5-prolog.html">Assigment 4 (Prolog) </a></li>
<li> <a href="CSC344hw5-python.html">Assigment 5 (Python) </a></li>
</ul>
</body>
</html>"""

mainHtmlFile.write(mainMessage)
mainHtmlFile.close()

#C html
cMessage = """<html>
<head><title> C project </title></head>
<body> <h1> Assignment 1 summary (C) </h1>
<h3> Files </h3>
<ul> 
<li> <a href="main.c"> main.c</a> - Lines:"""+linecount(cFile)+"""</li>
</ul>
<h3> Identifiers </h3>
<blockquote> test""" + processForHtmlfile(getIdentifiers(cFileOpened)) + """</blockquote>
</body></html>"""

cHtml.write(cMessage)
cHtml.close()


#clojure html
clojureMessage= """<html>
<head><title> Clojure project </title></head>
<body> <h1> Assignment 2 summary (Clojure) </h1>
<h3> Files </h3>
<ul> 
<li> <a href="core.clj"> core.clj</a> - Lines:""" + linecount(clojureFile)+ """</li>
</ul>
<h3> Identifiers </h3>
<blockquote>""" + processForHtmlfile(getIdentifiers(clojureFile)) + """"</blockquote>
</body>
</html>
"""

clojureHtml.write(clojureMessage)
clojureHtml.close()

#scala html
scalaMessage ="""<html>
<head><title> Scala project </title></head>
<body> <h1> Assignment 3 summary (Scala) </h1>
<h3> Files </h3>
<ul> 
<li> <a href="main.scala"> main.scala </a> - Lines:"""+ linecount(scalaFile)+"""</li>
</ul>
<h3> Identifiers </h3>
<blockquote>""" + processForHtmlfile(getIdentifiers(scalaFileOpened)) + """"</blockquote>
</body>
</html>
"""

scalaHtml.write(scalaMessage)
scalaHtml.close()

#prolog html
prologMessage = """<html>
<head><title> Prolog project </title></head>
<body> <h1> Assignment 4 summary (Prolog) </h1>
<h3> Files </h3>
<ul> 
<li> <a href="csc344hw04.pl""> csc344hw04.pl </a>- Lines:""" + linecount(prologFile) + """</li>
</ul>
<h3> Identifiers </h3>
<blockquote>""" + processForHtmlfile(getIdentifiers(prologFileOpened)) + """"</blockquote>
</body>
</html>
"""

prologHtml.write(prologMessage)
prologHtml.close()

#python html
pythonMessage = """<html>
<head><title> Python project </title></head>
<body> <h1> Assignment 5 summary (python) </h1>
<h3> Files </h3>
<ul> 
<li> <a href="main.py"> main.py </a> - Lines:""" + linecount(pythonFile)+ """</li>
</ul>
<h3> Identifiers </h3>
<blockquote>""" + processForHtmlfile(getIdentifiers(pythonFileOpened)) + """"</blockquote>
</body>
</html>
"""

pythonHtml.write(pythonMessage)
pythonHtml.close()




#zip and email portion

def zip(src, dst):
    zf = zipfile.ZipFile("%s.zip" % (dst), "w", zipfile.ZIP_DEFLATED)
    abs_src = os.path.abspath(src)
    for dirname, subdirs, files in os.walk(src):
        for filename in files:
            absname = os.path.abspath(os.path.join(dirname, filename))
            arcname = absname[len(abs_src) + 1:]
            zf.write(absname, arcname)
    zf.close()


f = "/Users/cedrichansen/IdeaProjects/csc344hw05"
d = f

#this line below actually makes the zipped file
zip(f,d)


#everything below actually emails the file
email_user = raw_input("Enter sender email: ")
email_password = raw_input("Enter password: ")
email_send = raw_input("Enter recipient email: ")
subject = "CSC344hw05 - Cedric Hansen"
msg = MIMEMultipart()
msg['From'] = email_user
msg['To'] = email_send
msg['Subject'] = subject
body = "Attached is the .zip file which contains all files for this assignment     "
msg.attach(MIMEText(body,'plain'))
filename = d + ".zip"
attachment = open(filename,'rb')
part = MIMEBase('application','octet-stream')
part.set_payload((attachment).read())
encoders.encode_base64(part)
part.add_header('Content-Disposition',"attachment; filename= "+filename)
msg.attach(part)
text = msg.as_string()
server = smtplib.SMTP('smtp.gmail.com',587)
server.starttls()
server.login(email_user,email_password)
server.sendmail(email_user,email_send,text)
server.quit()


'''
/Users/cedrichansen/IdeaProjects/csc344hw05
/Users/cedrichansen/IdeaProjects/csc344hw05/main.c
/Users/cedrichansen/IdeaProjects/csc344hw05.zip


list all words in a file with occurences -  
cat main.c | sed 's|[,.]||g' | tr ' ' '\n' | sort | uniq -c


'''