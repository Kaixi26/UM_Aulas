-module(chat).
-export([start_server/0, start_server/1]).

start_server() ->
    start_server(12345).

start_server(Port) ->
  {ok, LSock} = gen_tcp:listen(Port, [binary, {active, true}, {reuseaddr, true}]),
  RoomManager = spawn(fun() -> room_manager(#{}, []) end),
  spawn(fun() -> main_loop(LSock, RoomManager) end),
  ok.

main_loop(LSock, RoomManager) ->
    {ok, Sock} = gen_tcp:accept(LSock),
    io:format("accepted connection.~n"),
    RoomManager ! {new_user, self()},
    spawn(fun() -> main_loop(LSock, RoomManager) end),
    user(Sock, RoomManager).

room_manager(Rooms, Users) ->
    RoomCharacters = lists:seq($a,$z) ++ lists:seq($A,$Z) ++ lists:seq($0, $9),
    receive
        {new_user, User} ->
            User ! {msg, <<"Welcome to the main room!\n">>},
            io:format("RoomMan: new user has entered ~n"),
            room_manager(Rooms, [User | Users]);
        {say, {<<"\\enter ", Rest/binary>>, User}} ->
            {RoomName, _} = string:take(Rest, RoomCharacters),
            case maps:get(RoomName, Rooms, no_room) of
                no_room ->
                    io:format("RoomMan: creating new room ~p~n", [RoomName]),
                    Self = self(),
                    Room = spawn(fun() -> room(Self, [User]) end),
                    User ! {new_room, Room},
                    room_manager(maps:put(RoomName, Room, Rooms), Users -- [User]);
                Room ->
                    io:format("RoomMan: adding user to room ~p~n", [RoomName]),
                    Room ! {new_user, User},
                    User ! {new_room, Room},
                    room_manager(Rooms, Users -- [User])
            end;
        {say, {<<"\\quit", _/binary>>, User}} ->
            User ! {msg, <<"Bye!\n">>},
            User ! quit,
            room_manager(Rooms, Users -- [User]);
        {say, {Data, _}} ->
            io:format("RoomMan: ~p~n", [Data]),
            [User ! {msg, Data} || User <- Users],
            room_manager(Rooms, Users);
        _ ->
            room_manager(Rooms, Users)
    end.

room(RoomManager, Users) ->
    receive
        {new_user, User} ->
            User ! {msg, <<"Entered new room!\n">>},
            room(RoomManager, [User | Users]);
        {say, {<<"\\quit", _/binary>>, User}} ->
            io:format("Room[~p][~p]: user ~p quit~n", [RoomManager, self(), User]),
            User ! {new_room, RoomManager},
            RoomManager ! {new_user, User},
            room(RoomManager, Users -- [User]);
        {say, {Data, _}} ->
            io:format("Room[~p][~p]: ~p~n", [RoomManager, self(), Data]),
            [User ! {msg, Data} || User <- Users],
            room(RoomManager, Users);
        _ ->
            room(RoomManager, Users ++ Users)
    end.


user(Sock, Room) ->
    receive
        {tcp, _, Data} ->
%           io:format("[~p] received ~p~n", [self(), Data]),
            Room ! {say, {Data, self()}},
            user(Sock, Room);
        {msg, Data} ->
            gen_tcp:send(Sock, Data),
            user(Sock, Room);
        {new_room, NewRoom} ->
            io:format("Client[~p] entering new room ~p~n", [self(), NewRoom]),
            user(Sock, NewRoom);
        quit ->
            ok;
        _ ->
            io:format("invalid message received~n"),
            user(Sock, Room)
    end.

%serve_client(Sock) ->
%    receive
%        {tcp, _, Data} ->
%            io:format("received ~p~n", [Data]),
%            gen_tcp:send(Sock, Data),
%            serve_client(Sock);
%        {tcp_closed, Sock} ->
%            io:format("user disconnected~n", []),
%            gen_tcp:close(Sock);
%        {tcp_error, Sock, _} ->
%            io:format("tcp error~n", []),
%            gen_tcp:close(Sock)
%    end.
