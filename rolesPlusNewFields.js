//Datafix for adding user role for those users who did not have it
//Also, adds new fields, sets them to true and adds predefined email addresses
db.users.find({"roles.$id":{$ne:ObjectId("567300a489c6fb016a1cde31")}}).forEach( function (doc) {db.users.update({"username":doc.username},{$addToSet:{roles:{"$ref": "roles","$id": ObjectId("567300a489c6fb016a1cde31")}}})});

db.users.find({"active":{$ne:true}}).forEach( function (doc) {db.users.update({"username":doc.username},{$set:{active:true}})});

db.users.find({"emailConfirmed":{$ne:true}}).forEach( function (doc) {db.users.update({"username":doc.username},{$set:{emailConfirmed:true}})});

db.users.find().forEach( function (doc){
    doc.email = doc.fullName;
    doc.email = doc.email.replace(" ", ".").toLowerCase().concat("@swedbank.lt");
    doc.email = doc.email.replace(/ą/gi, "a");
    doc.email = doc.email.replace(/č/gi, "c");
    doc.email = doc.email.replace(/ę/gi, "e");
    doc.email = doc.email.replace(/ė/gi, "e");
    doc.email = doc.email.replace(/į/gi, "i");
    doc.email = doc.email.replace(/š/gi, "s");
    doc.email = doc.email.replace(/ų/gi, "u");
    doc.email = doc.email.replace(/ū/gi, "u");
    doc.email = doc.email.replace(/ž/gi, "z");
    db.users.save(doc);
})