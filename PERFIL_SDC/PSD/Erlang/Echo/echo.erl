-module(echo).
-export([start_server/1]).

start_server(Port) ->
  {ok, LSock} = gen_tcp:listen(Port, [binary, {packet, line}, {reuseaddr, true}]),
  spawn(fun() -> main_loop(LSock) end),
  ok.

main_loop(LSock) ->
    {ok, Sock} = gen_tcp:accept(LSock),
    io:format("accepted connection.~n"),
    Pid = spawn(fun() -> serve_client(Sock) end),
    gen_tcp:controlling_process(Sock, Pid),
    main_loop(LSock).

serve_client(Sock) ->
    receive
        {tcp, _, Data} ->
            io:format("received ~p~n", [Data]),
            gen_tcp:send(Sock, Data),
            serve_client(Sock);

        {tcp_closed, Sock} ->
            io:format("user disconnected~n", []),
            gen_tcp:close(Sock);

        {tcp_error, Sock, _} ->
            io:format("tcp error~n", []),
            gen_tcp:close(Sock)
    end.
