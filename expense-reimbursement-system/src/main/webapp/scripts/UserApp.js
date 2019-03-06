window.onload = function () {
    loadHomeView();
}

function loadRegisterView(){
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4){
            $('#view').html(xhr.responseText);
            $('#gotoreg').on('click', loadLogInView);
            $('#register').on('click', register);
        }
    }
    xhr.open('GET', 'register.view');
    xhr.send();
}

function register(){
    let userdata = {
        username: $('#username').val(),
        password: $('#pw').val(),
        confirm: $('#cfpw').val(),
        firstname: $('#firstname').val(),
        lastname: $('#lastname').val(),
        email: $('#email').val()
    };
    let json = JSON.stringify(userdata);
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4){
            if (xhr.status == 200) {
                $('#username').removeClass('btn-danger');
                $('#email').removeClass('btn-danger');
                $('#pw').removeClass('btn-danger');
                $('#cfpw').removeClass('btn-danger');
                $('#errormsg').html('');
                loadLogInView();
            } else if (xhr.status == 406) {
                $('#username').removeClass('btn-danger');
                $('#email').removeClass('btn-danger');
                $('#pw').removeClass('btn-danger');
                $('#cfpw').removeClass('btn-danger');
                $('#firstname').removeClass('btn-danger');
                $('#lastname').removeClass('btn-danger');
                $('#errormsg').html(xhr.responseText);
                if (xhr.responseText == `Passwords don't match`) {
                    $('#pw').addClass('btn-danger');
                    $('#cfpw').addClass('btn-danger');
                } else if (xhr.responseText == `User exists`) {
                    $('#username').addClass('btn-danger');
                } else if (xhr.responseText == `Email was already used`) {
                    $('#email').addClass('btn-danger');
                } else if (xhr.responseText == `User Required`) {
                    $('#username').addClass('btn-danger');
                } else if (xhr.responseText == `Email Required`) {
                    $('#email').addClass('btn-danger');
                } else if (xhr.responseText == `Password Required`) {
                    $('#pw').addClass('btn-danger');
                } else if (xhr.responseText == `First Name Required`) {
                    $('#firstname').addClass('btn-danger');
                } else if (xhr.responseText == `Last Name Required`) {
                    $('#lastname').addClass('btn-danger');
                }
            }
        }
    }
    xhr.open('POST', 'createuser');
    xhr.setRequestHeader('Content-type', 'application/json');
    xhr.send(json);
}

function initHome() {
    $('#btnAll').on('click', filterAll);
    $('#btnPending').on('click', filterPending);
    $('#btnResolved').on('click', filterResolved);

    $('#logout').on('click', logout);
    paginationInit();
    loadHomeData(1, 'All');
}

function loadHomeView(){
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4){
            if (xhr.status == 200) {
                $('#view').html(xhr.responseText);
                $('#logout').removeAttr('hidden');
                initHome();
            }
            else if (xhr.status == 406) {
                loadLogInView();
            } 
        }
    }
    xhr.open('GET', 'home');
    xhr.send();
}

function loadLogInView(){
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4){
            $('#view').html(xhr.responseText);
            $('#gotosubmit').on('click', loadRegisterView);
            $('#login').on('click', login);
            $('#pw').bind("enterKey",login);
            $('#pw').keyup(function(e){
                if(e.keyCode == 13)
                {
                    $(this).trigger("enterKey");
                }
            });
            $('#logout').prop('hidden', true);
        }
    }
    xhr.open('GET', 'login.view');
    xhr.send();
}

function logout() {
    let xhr = new XMLHttpRequest();
    //glyphicon glyphicon-log-in
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4){
            if (xhr.status == 200) {
                $('#view').html(xhr.responseText);
                $('#logout').prop('hidden', true);
                loadLogInView();
            }
        }
    }
    xhr.open('GET', 'logout');
    xhr.send();
}

function login(){
    let ErsUser = {
        ersUsername: $('#username').val(),
        ersPassword: $('#pw').val(),
    };

    let json = JSON.stringify(ErsUser);
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4){
            if (xhr.status == 200) {
                $('#view').html(xhr.responseText);
                $('#logout').removeAttr('hidden');
                initHome();
            }
            else if (xhr.status == 406) {
                console.log("Invalid Login");
            } else {
                console.log("Invalid Password");
            }
        }
    }
    xhr.open('POST', 'login');
    xhr.setRequestHeader('Content-type', 'application/json');
    xhr.send(json);
}

function filterAll(){
    $('#filter').html('All');
    filterReimbData();
}

function filterPending(){
    $('#filter').html('Pending');
    filterReimbData();
}

function filterResolved(){
    $('#filter').html('Resolved');
    filterReimbData();
}

function filterReimbData(){
    loadHomeData(1, $('#filter').html());
}

function paginationInit(){
    $('#pages').on('click', 'li', function(){
        let num = parseInt(this.firstChild.innerHTML);
        let str = this.firstChild.innerHTML;
        let current = parseInt($('#pageNum').html());
        let length = parseInt($('#pageLength').html());
        if (Boolean(num)) {
            console.log(`Number status info: ${num}`);
            loadHomeData(num, $('#filter').html())
        }
        else {
            console.log(`Status info: ${str}`);
            if (str == 'Next') loadHomeData(current + 1 < length ? current + 1 : current, $('#filter').html());
            else loadHomeData(current - 1 > 1 ? current - 1 : 1, $('#filter').html());
        }
    });
}

function loadHomeData(page, type){
    let dataInfo = {
        page: page, 
        filter: type
    }

    let json = JSON.stringify(dataInfo);
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4){
            if (xhr.status == 200) {
                let tableData = JSON.parse(xhr.responseText);
                console.log(tableData);
                $('#greet').html(`Welcome ${tableData.user.userFirstName} ${tableData.user.userLastName}`);
                console.log(tableData.reimbursement);
                let length = ((tableData.reimbursement.length / 10) >> 0) + 1;
                $('#pageNum').html(dataInfo.page);
                $('#pageLength').html(length);
                console.log(length);
                let info = ``;
                for (let infoItem of tableData.reimbursement) {
                    info += '<tr>';
                    info += `<td hidden>${infoItem.reimb_ID}</td>`;
                    info += `<td>$${infoItem.reimb_amount}</td>`;
                    info += `<td>${infoItem['reimbAuthor'].userFirstName}</td>`;
                    info += `<td>${infoItem['reimbAuthor'].userLastName}</td>`;
                    info += `<td>${infoItem.reimbDescription}</td>`;
                    if (infoItem.reimb_Status == 'Pending') {
                        if (tableData.financialManager){
                            info += `<td><button type="button" class="btn btn-success" data-toggle="modal" data-target="#pendingModal">${infoItem.reimb_Status}</button></td>`;
                        } else {
                            info += `<td>${infoItem.reimb_Status}</td>`;
                        }
                    } else {
                        if (infoItem.reimb_Status == 'Approved') {
                            info += `<td>${infoItem.reimb_Status}</td>`;
                        } else {
                            info += `<td>${infoItem.reimb_Status}</td>`;
                        }
                    }
                    info += `<td>${infoItem.reimb_Type}</td>`;
                    let submittedDateDisplay = new Date(infoItem.reimb_submitted);
                    info += `<td>${submittedDateDisplay.getMonth()}/${submittedDateDisplay.getDay()}/${submittedDateDisplay.getFullYear()} ${submittedDateDisplay.getHours()}:${submittedDateDisplay.getMinutes()}:${submittedDateDisplay.getSeconds()}</td>`;
                    let ResolvedDateDisplay = new Date(infoItem.reimb_submitted);
                    info += `<td>${ResolvedDateDisplay.getMonth()}/${ResolvedDateDisplay.getDay()}/${ResolvedDateDisplay.getFullYear()} ${ResolvedDateDisplay.getHours()}:${ResolvedDateDisplay.getMinutes()}:${ResolvedDateDisplay.getSeconds()}</td>`;
                    info += '</tr>';
                }
                $('#data').html(info);
                if (tableData.financialManager) {
                    console.log($('#pendingModal'));
                    $('#data').on('click', 'button', function(){
                        let ID = this.parentElement.previousSibling.previousSibling.previousSibling.previousSibling.previousSibling.innerHTML;
                        let statusElement = this.parentElement;
                        let reimblist = tableData.reimbursement;
                        let data = reimblist.find(item => item.reimb_ID == ID);
                        console.log(data);
                        $('#reimbTitle').html(`Reimbursement request from <br>${data.reimbAuthor.userFirstName} ${data.reimbAuthor.userLastName}`);
                        $('#reimbemail').html(`<h5>${data.reimbAuthor.userEmail}</h5>`);
                        $('#amount').html(`<h5>$${data.reimb_amount}</h5>`);
                        $('#desc').html(`<h5>${data.reimbDescription}</h5>`);
                        let submittedDate = new Date(data.reimb_submitted);
                        $('#submitted').html(`<h5>${submittedDate.getMonth()}/${submittedDate.getDay()}/${submittedDate.getFullYear()} ${submittedDate.getHours()}:${submittedDate.getMinutes()}:${submittedDate.getSeconds()}</h5>`);
                        $('#type').html(`<h5>${data.reimb_Type}</h5>`);
                        function Resolve(resolveStatus) {
                            statusElement.innerHTML = `<button class="btn btn-primary" type="button" disabled>
                                <span class="fa fa-spinner fa-pulse fa-lg"></span>
                                Loading...
                            </button>`;
    
                            let xhrResolver = new XMLHttpRequest();
                            xhrResolver.onreadystatechange = function() {
                                if (xhrResolver.readyState == 4) {
                                    if (xhrResolver.status != 200) {
                                        statusElement.innerHTML = `<td><button type="button" class="btn btn-success" data-toggle="modal" data-target="#pendingModal">Pending</button></td>`;
                                        alert(xhrResolver.responseText);
                                    } else {
                                        console.log($('#pendingModal'));
                                        statusElement.innerHTML = `<td>${xhrResolver.responseText}</td>`;
                                    }
                                }
                            }
                            xhrResolver.open('POST', 'resolve');
                            xhrResolver.setRequestHeader('Content-type', 'application/text');
                            xhrResolver.send(resolveStatus);
                        }
                        $('#reimbApprove').on('click', function() {Resolve('Approve,' + ID)});
                        $('#reimbDeny').on('click', function() {Resolve('Deny,' + ID)});
                    });
                } else {
                    $('#reimbSubmit').on('click', function(){
                        let ErsReimbursement = {
                            reimb_amount: $('#amount').val(),
                            reimbDescription: $('#desc').val(),
                            reimb_Type: $('#btnType').html()
                        };
                        console.log(ErsReimbursement);
                        let json = JSON.stringify(ErsReimbursement);

                        let xhrResolver = new XMLHttpRequest();
                        xhrResolver.onreadystatechange = function() {
                            if (xhrResolver.readyState == 4) {
                                if (xhrResolver.status != 200) {
                                    alert(xhrResolver.responseText);
                                } else {
                                    loadHomeData(page, type);
                                }
                            }
                        }
                        xhrResolver.open('POST', 'create');
                        xhrResolver.setRequestHeader('Content-type', 'application/json');
                        xhrResolver.send(json);
                    });
                }
                let pageItems = `<li class="page-item"><a class="page-link">Previous</a></li>`;
                for (let i = 1; i <= length; i++) {
                    if (i === dataInfo.page)
                        pageItems += `<li class="page-item active"><a class="page-link">${i}</a></li>`;
                    else
                        pageItems += `<li class="page-item"><a class="page-link">${i}</a></li>`;
                }
                pageItems += `<li class="page-item"><a class="page-link">Next</a></li>`;
                $('#pages').html(pageItems);
            }
            else if (xhr.status == 406) {
                console.log(xhr.responseText);
                loadLogInView();
            }
            else if (xhr.status == 303){
                loadHomeView();
            }
        }
    }
    xhr.open('POST', 'home');
    xhr.setRequestHeader('Content-type', 'application/json');
    xhr.send(json);
}