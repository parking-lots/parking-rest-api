//script inserts subdocument availablePeriods with prefilled values to those lots which are available in the future
//script also removes deprecated fields freeFrom and freeTill from all documents

db.lots.find({"freeTill":{$gt:new Date()}}).forEach( function (doc) {
                                                    db.lots.update({"number":doc.number},{$set:{availablePeriods:[{freeFrom:doc.freeFrom,freeTill:doc.freeTill}]}})
                                     });

db.lots.update({},{$unset:{freeFrom:"",freeTill:""}},{multi: true});
