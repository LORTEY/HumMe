import spotipy
from spotipy.oauth2 import SpotifyClientCredentials
from dataclasses import dataclass, asdict
from dataclasses_json import dataclass_json
import json
import requests

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
    id:str
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
    global sp
    if sp is None:
        return "Spotify client not initialized"

    try:
        type = playlist_url.split('/')[-2]
        playlist_id = playlist_url.split('/')[-1].split('?')[0]

    except:
        return "Wrong Spotify Link"
    try:
        match (type):
            case "album":

                all_tracks = []
                album_tracks = sp.album_tracks(playlist_id)
                all_tracks.extend(album_tracks["items"])

                # Handle pagination for large albums
                while album_tracks["next"]:
                    album_tracks = sp.next(album_tracks)
                    all_tracks.extend(album_tracks["items"])

                playlist_data = Playlist(
                    name=sp.album(playlist_id)['name'],
                    tracks=[
                        Track(
                            name=track["name"],
                            id=track["id"],
                            artist=[artist["name"] for artist in track["artists"]]
                        )
                        for track in all_tracks
                    ]
                )
                return playlist_data.to_json()

            case "playlist":
                playlist = sp.playlist(playlist_id)
                playlistData = Playlist(playlist["name"],[])
                results = sp.playlist_items(playlist_id)
                tracks = results['items']

            case "artist":
                return (get_all_artist_tracks(playlist_url))
    except requests.exceptions.ConnectionError as e:
        return "Connection Error"
    except spotipy.exceptions.SpotifyException:
        return "Wrong Spotify Link"
    except spotipy.exceptions.SpotifyOauthError:
        return "Spotify client not initialized"
    for item in tracks:
        track = item['track']
        playlistData.tracks.append(Track(track['name'], track['id'] , [artist["name"] for artist in track['artists']]))

    return playlistData.to_json()

def get_all_artist_tracks(artist_url):
    global sp
    if not sp:
        return "Spotify client not initialized"

    try:
        # Extract artist ID from URL
        artist_id = artist_url.split("/")[-1].split("?")[0]

        # Get artist name
        artist = sp.artist(artist_id)
        artist_name = artist["name"]

        # Get all albums (including singles and compilations)
        albums = []
        results = sp.artist_albums(artist_id, album_type="album,single,compilation", limit=50)
        albums.extend(results["items"])

        # Handle pagination (if more than 50 albums)
        while results["next"]:
            results = sp.next(results)
            albums.extend(results["items"])

        # Get all tracks from each album
        all_tracks = []
        for album in albums:
            album_tracks = sp.album_tracks(album["id"])
            all_tracks.extend(album_tracks["items"])

            # Handle pagination for large albums
            while album_tracks["next"]:
                album_tracks = sp.next(album_tracks)
                all_tracks.extend(album_tracks["items"])

        # Remove duplicate tracks (same ID)
        unique_tracks = {}
        for track in all_tracks:
            if track["id"] not in unique_tracks:
                unique_tracks[track["id"]] = track

        # Prepare the playlist data
        playlist_data = Playlist(
            name=artist_name,
            tracks=[
                Track(
                    name=track["name"],
                    id=track["id"],
                    artist=[artist["name"] for artist in track["artists"]]
                )
                for track in unique_tracks.values()
            ]
        )

        return playlist_data.to_json()
    except requests.exceptions.ConnectionError:
        return "Connection Error"
    except spotipy.exceptions.SpotifyException:
        return "Wrong Spotify Link"
    except spotipy.exceptions.SpotifyOauthError:
        return "Spotify client not initialized"
    except Exception as e:
        return f"Error: {str(e)}"