//Datafix for adding user role for those users who did not have it
//Also, adds new fields, sets them to true and adds predefined email addresses
db.users.find({"roles.$id":{$ne:ObjectId("567300a489c6fb016a1cde31")}}).forEach( function (doc) {db.users.update({"username":doc.username},{$addToSet:{roles:{"$ref": "roles","$id": ObjectId("567300a489c6fb016a1cde31")}}})});

db.users.find({"active":{$ne:true}}).forEach( function (doc) {db.users.update({"username":doc.username},{$set:{active:true}})});

db.users.find({"emailConfirmed":{$ne:true}}).forEach( function (doc) {db.users.update({"username":doc.username},{$set:{emailConfirmed:true}})});

db.users.find({"email":{$eq:null}}).forEach( function (doc) {db.users.update({"username":doc.username},{$set:{email:doc.fullName}})});

db.users.find({"email":{$ne:null}}).forEach( function (doc){
    doc.email = doc.email.replace(" ", ".").toLowerCase().concat("@swedbank.lt");
    doc.email = doc.email.replace("ą", "a");
    doc.email = doc.email.replace("č", "c");
    doc.email = doc.email.replace("ę", "e");
    doc.email = doc.email.replace("ė", "e");
    doc.email = doc.email.replace("į", "i");
    doc.email = doc.email.replace("š", "s");
    doc.email = doc.email.replace("ų", "u");
    doc.email = doc.email.replace("ū", "u");
    doc.email = doc.email.replace("ž", "z");
    db.users.save(doc);
})