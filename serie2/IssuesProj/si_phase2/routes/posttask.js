var express = require('express');
var router = express.Router();
const usersMap = require("../public/UsersData");
const request = require("axios");
const CLIENT_ID = "747819205262-uteoim61ntfqe29s4bs8huqjebn6tqr6.apps.googleusercontent.com";


router.get('/*', async function (req, res, next) {
    let issue = JSON.parse(req.params[0]);
    let user = usersMap.getUser(issue.username);
    let googleAccessToken = user.google_access_token;
    let url = 'https://www.googleapis.com/tasks/v1/users/@me/lists?' + 'key=' + CLIENT_ID;

    //get if or create a new tasklist ang return new id
    let id = await promiseTaskListId(googleAccessToken);
    console.log("id ", id);

    //let task = await insertTask(googleAccessToken, id, issue);

    //res.sendFile('./views/task');
    //res.status(200);

    res.send('test');

});

const promiseTaskListId = async (access_token) => {
    const response = await request.get(
        'https://www.googleapis.com/tasks/v1/users/@me/lists?' + 'key=' + CLIENT_ID, {
            headers: {
                "Authorization": "Bearer " + access_token,
                accept: 'application/json'
            }
        });
    if (response.data.items === 0) {
        return createNewTaskList(access_token);
    }
    return response.data.items[0]["id"];
};

function createNewTaskList(access_token) {

}

const insertTask = async (accessToken, taskListId, issue) => {
    try {
        const response = await request.post(
            'https://www.googleapis.com/tasks/v1/lists/tasklist/tasks?tasklist=' + taskListId + '&Client_id=' + CLIENT_ID,
            {
                title: issue.title,
                notes: issue.body
            },
            {
                headers: {
                    Authorization: "Bearer " + accessToken
                }
            });
        return response.data;
    } catch (e) {
        console.log(e)
    }

};

module.exports = router;