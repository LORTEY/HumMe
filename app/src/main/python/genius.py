from lyricsgenius import Genius
import requests

genius = None
def set_global_genius(token):
    try:
        global genius
        genius = Genius(token)
        return True
    except:
        return False

def get_lyrics(author, song_name):
        try:
            song = genius.search_song(song_name,author)
            if song is not None:
                return song.lyrics
            else:
                return "ERROR: SONG NOT FOUND BY GENIUS"
        except(TimeoutError):
            return "ERROR: REQUEST TIMED OUT"
        except requests.exceptions.ConnectionError:
            # likely no internet connection
            return "ERROR: NO CONNECTION"
        except ConnectionError:
            # likely no internet connection
            return "ERROR: NO CONNECTION"
        except requests.exceptions.HTTPError:
            # likely wrong api
            return "ERROR: WRONG API"

