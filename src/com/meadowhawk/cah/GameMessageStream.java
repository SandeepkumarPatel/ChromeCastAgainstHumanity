/*
 * Copyright (C) 2013 Google Inc. All Rights Reserved. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.meadowhawk.cah;

import com.google.cast.MessageStream;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * An abstract class which encapsulates control and game logic for sending and receiving messages 
 * during a TicTacToe game.
 */
public abstract class GameMessageStream extends MessageStream {
    private static final String TAG = GameMessageStream.class.getSimpleName();

    private static final String GAME_NAMESPACE = "com.google.chromecast.demo.tictactoe";

    public static final String END_STATE_X_WON = "X-won";
    public static final String END_STATE_O_WON = "O-won";
    public static final String END_STATE_DRAW = "draw";
    public static final String END_STATE_ABANDONED = "abandoned";

    public static final String PLAYER_X = "X";
    public static final String PLAYER_O = "O";

    
    /**
     * List of expected messages that the remote will accept.
     *
     */
    public enum SEND_MESSAGES {
    	JOIN,DROPOUT,CARD_REQUEST,PLAY_CARDS;
    }
    
    // Receivable event types
    public enum RECIEVE_MESSAGES {
    	PLAYER_JOIN, PLAYER_DROP, ERROR, GAME_STATUS_UPDATE, CARD_PLAYED, GOT_CARDS;

		public static RECIEVE_MESSAGES getByString(String msg) {
			for (RECIEVE_MESSAGES recvMesg : RECIEVE_MESSAGES.values()) {
				if(recvMesg.name().equalsIgnoreCase(msg)){
					return recvMesg;
				}
			}
			return ERROR;
		} 
    }

    public enum STATUS_UPDATE{
    	END_GAME, NEXT_ROUND_START, GOT_AWESOME,NONE;

		public static STATUS_UPDATE getByString(String status) {
			for (STATUS_UPDATE statUpdate : STATUS_UPDATE.values()) {
				if(statUpdate.name().equalsIgnoreCase(status)){
					return statUpdate;
				}
			}
			return NONE;
		}
    }
    
//    private static final String KEY_BOARD_LAYOUT_RESPONSE = "board_layout_response";
    private static final String KEY_EVENT = "event";
//    private static final String KEY_JOINED = "joined";
//    private static final String KEY_MOVED = "moved";
//    private static final String KEY_ENDGAME = "endgame";
//    private static final String KEY_ERROR = "error";

    // Commands
    private static final String KEY_COMMAND = "command";

    private static final String KEY_MESSAGE = "message";
    private static final String KEY_NAME = "name";
    private static final String KEY_CARDS = "cards";
    private static final String KEY_OPPONENT = "opponent";
    private static final String KEY_PLAYER = "player";

	private static final String STATUS_TYPE = "status_type";

    /**
     * Constructs a new GameMessageStream with GAME_NAMESPACE as the namespace used by 
     * the superclass.
     */
    protected GameMessageStream() {
        super(GAME_NAMESPACE);
    }

    /**
     * Performs some action upon a player joining the game.
     * 
     * @param playerSymbol either X or O
     * @param opponentName the name of the player who just joined an existing game, or the opponent
     */
    protected abstract void onGameJoined(String playerSymbol, String opponentName);

    /**
     * Adds new cards to players Hand.
     * 
     * @param list of cards.
     */
    protected abstract void onGotCards(JSONArray cards);

    /**
     * Performs some action upon a game error.
     * 
     * @param errorMessage the string description of the error
     */
    protected abstract void onGameError(String errorMessage);

    /**
     * 
     * @param newStatus
     */
    protected abstract void onGameStatusUpdate(STATUS_UPDATE newStatus) ;

    /**
     * 
     * @param player
     */
    protected abstract void onPlayerDrop(String player);
    
    /**
     * Attempts to connect to an existing session of the game by sending a join command.
     * 
     * @param name the name of the player that is joining
     */
    public final void join(String name) {
        try {
        	//TODO: update to send proper message
            Log.d(TAG, "join: " + name);
            JSONObject payload = new JSONObject();
            payload.put(KEY_COMMAND, SEND_MESSAGES.JOIN.toString());
            payload.put(KEY_NAME, name);
            sendMessage(payload);
        } catch (JSONException e) {
            Log.e(TAG, "Cannot create object to join a game", e);
        } catch (IOException e) {
            Log.e(TAG, "Unable to send a join message", e);
        } catch (IllegalStateException e) {
            Log.e(TAG, "Message Stream is not attached", e);
        }
    }

	public final void submitCards(List<Object> cards){
		//TODO: update to send proper message
		try {
        	//TODO: update to send proper message
            Log.d(TAG, "submitCards: " + cards.size());
            JSONObject payload = new JSONObject();
            payload.put(KEY_COMMAND, SEND_MESSAGES.PLAY_CARDS.toString());
            payload.put(KEY_CARDS, cards);
            sendMessage(payload);
        } catch (JSONException e) {
            Log.e(TAG, "Cannot create object to submit cards", e);
        } catch (IOException e) {
            Log.e(TAG, "Unable to send submitcard message", e);
        } catch (IllegalStateException e) {
            Log.e(TAG, "Message Stream is not attached", e);
        }
	}
	
	public final void playNextHand(){
		//perhaps submitCards will just request more cards to make  simpler UI?
		try {
        	//TODO: update to send proper message
            Log.d(TAG, "requestCards");
            JSONObject payload = new JSONObject();
            payload.put(KEY_COMMAND, SEND_MESSAGES.CARD_REQUEST.toString());
            sendMessage(payload);
        } catch (JSONException e) {
            Log.e(TAG, "Cannot create object to request cards", e);
        } catch (IOException e) {
            Log.e(TAG, "Unable to send a card request", e);
        } catch (IllegalStateException e) {
            Log.e(TAG, "Message Stream is not attached", e);
        }
	}

    /**
     * Sends a command to leave the current game.
     */
    public final void leave(String name) {
        try {
            Log.d(TAG, "leave");
            JSONObject payload = new JSONObject();
            payload.put(KEY_COMMAND, SEND_MESSAGES.DROPOUT.toString());
            payload.put(KEY_NAME, name);
            sendMessage(payload);
        } catch (JSONException e) {
            Log.e(TAG, "Cannot create object to leave a game", e);
        } catch (IOException e) {
            Log.e(TAG, "Unable to send a leave message", e);
        } catch (IllegalStateException e) {
            Log.e(TAG, "Message Stream is not attached", e);
        }
    }

    /**
     * Processes all JSON messages received from the receiver device and performs the appropriate 
     * action for the message. Recognizable messages are of the form:
     * 
     * <p>No other messages are recognized.
     */
    @Override
    public void onMessageReceived(JSONObject message) {
        try {
            Log.d(TAG, "onMessageReceived: " + message);
            if (message.has(KEY_EVENT)) {
                RECIEVE_MESSAGES msgRecieved = RECIEVE_MESSAGES.getByString(message.getString(KEY_EVENT));
            
				switch (msgRecieved) {
				case PLAYER_JOIN:
					Log.d(TAG, "JOINED");
                    try {
                        String player = message.getString(KEY_PLAYER);
                        String opponentName = message.getString(KEY_OPPONENT);
                        onGameJoined(player, opponentName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
					break;
					
				case PLAYER_DROP:
					Log.d(TAG, "DROP");
                    try {
                        String player = message.getString(KEY_PLAYER);
                        //notify if a play drops the game.
                        onPlayerDrop(player);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    
				case GAME_STATUS_UPDATE:   
					Log.d(TAG, "STATUS");
                    try {
                        String status = message.getString(STATUS_TYPE); //
                        STATUS_UPDATE newStatus = STATUS_UPDATE.getByString(status);
                        onGameStatusUpdate(newStatus);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    
				case CARD_PLAYED:
					Log.d(TAG, "CARD_PLAYED");
					
				case GOT_CARDS:
					Log.d(TAG, "GOT_CARDS");
					try{
					    JSONArray cards = message.getJSONArray("cards");
						onGotCards(cards);
					}catch (JSONException e) {
                        e.printStackTrace();
                    }
					
				case ERROR:  
					
				default:
					Log.d(TAG, "ERROR");
                    try {
                        String errorMessage = message.getString(KEY_MESSAGE);
                        onGameError(errorMessage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
					break;
				}
     
            } else {
                Log.w(TAG, "Unknown message: " + message);
            }
        } catch (JSONException e) {
            Log.w(TAG, "Message doesn't contain an expected key.", e);
        }
    }

}
