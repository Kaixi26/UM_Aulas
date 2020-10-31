-module(login_manager).
-export([create/0, create_account/3, close_account/2, accounts/1, online/1]).

accounts() ->
    accounts.

logged() ->
    logged.

create() ->
    spawn(fun() -> login_manager(#{accounts() => [], logged() => []}) end).

login_manager_create_account({State, From}, {Username, Passwd}) ->
    Accounts = maps:get(accounts(), State),
    case lists:any(fun({Eusername, _}) -> Eusername = Username end, Accounts) of
        true ->
            From ! user_exists,
            login_manager(State);
        _ ->
            From ! ok,
            login_manager(maps:put(accounts(), ([{Username, Passwd}] ++ Accounts), State))
    end.

login_manager_close_account({State, From}, {Username}) ->
    Accounts = maps:get(accounts(), State),
    newAccounts = lists:filter(fun({Eusername, _}) -> Eusername /= Username end, Accounts),
    From ! ok,
    login_manager(maps:put(accounts(), newAccounts, State)).

login_manager_login({{State, From}, {Username, Passwd}}) ->
    Accounts = maps:get(accounts(), State),
    Logged = maps:get(logged(), State),
    ExistsAccount = lists:member({Username, Passwd}, Accounts),
    IsLogged = lists:member(Username, Logged),
    case {ExistsAccount, IsLogged} of
        {true, true} ->
            From ! ok,
            login_manager(maps:put(logged(), [Username] ++ Logged, Logged));
        _ ->
            From ! invalid,
            login_manager(State)
    end.
            
login_manager_logout({{State, From}, {Username}}) ->
    Logged = maps:get(logged(), State),
    From ! ok,
    login_manager(maps:put(logged(), Logged -- [Logged], Logged)).
            

login_manager(State) ->
    receive
        {{create, From}, {Username, Passwd}} ->
            login_manager_create_account({State, From}, {Username, Passwd});
        {{close, From}, {Username}} ->
            login_manager_close_account({State, From}, {Username});
        {{destroy, From}} ->
            From ! ok;
        {{login, From}, {Username, Passwd}} ->
            login_manager_login({{State, From}, {Username, Passwd}});
        {{logout, From}, {Username}} ->
            login_manager_login({{State, From}, {Username}});
        {{accounts, From}} ->
            From ! maps:get(accounts(), State),
            login_manager(State);
        {{online, From}} ->
            From ! maps:get(logged(), State),
            login_manager(State);
        _ ->
            error_logger:error_report("Invalid message received."),
            login_manager(State)
    end.

create_account(LoginManager, Username, Passwd) ->
    LoginManager ! {{create, self()}, {Username, Passwd}}.

close_account(LoginManager, Username) ->
    LoginManager ! {{close, self()}, {Username}}.

accounts(LoginManager) ->
    LoginManager ! {{accounts, self()}},
    receive
        List -> List
    end.

online(LoginManager) ->
    LoginManager ! {{online, self()}},
    receive
        List -> List
    end.
