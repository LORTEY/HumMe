import spotipy
from spotipy.oauth2 import SpotifyClientCredentials
from dataclasses import dataclass, asdict
from dataclasses_json import dataclass_json
import json

sp = None
@dataclass_json
@dataclass
class Playlist:
    name: str
    tracks: list

@dataclass_json
@dataclass
class Track:
    name:str
    artist:list

def set_global_sp(SPOTIPY_CLIENT_ID,SPOTIPY_CLIENT_SECRET):
    global sp
    try:
        sp = spotipy.Spotify(auth_manager=SpotifyClientCredentials(
            client_id=SPOTIPY_CLIENT_ID,
            client_secret=SPOTIPY_CLIENT_SECRET
        ))
        return True
    except:
        return False

def get_playlist_tracks(playlist_url):
    # Extract playlist ID from URL
    playlist_id = playlist_url.split('/')[-1].split('?')[0]

    # Get playlist details
    playlist = sp.playlist(playlist_id)

    playlistData = Playlist(playlist["name"],[])
    results = sp.playlist_items(playlist_id)
    tracks = results['items']

    for item in tracks:
        track = item['track']
        playlistData.tracks.append(Track(track['name'], [artist["name"] for artist in track['artists']]))

    return playlistData.to_json()