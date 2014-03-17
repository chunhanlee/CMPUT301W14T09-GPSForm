/**
 
License GPLv3: GNU GPL Version 3
<http://gnu.org/licenses/gpl.html>.
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ca.cmput301w14t09.model;

import java.io.Serializable;
import java.util.ArrayList;

import ca.cmput301w14t09.model.Favorite;
import ca.cmput301w14t09.model.UnreadMarker;


/**
 * Tracks the settings of the associated user.
 * @author mcmorris
 */
public class Profile implements Serializable {
	private ArrayList<Favorite> favorites;
	private java.util.ArrayList<UnreadMarker> unreadComments;
	private String authorName;
	private String userName;
	
	private static final long serialVersionUID = 2L;

	public Profile(String userName) {
		this.favorites = new ArrayList<Favorite>();
		this.unreadComments = new ArrayList<UnreadMarker>();
		this.authorName = userName;
		this.userName = userName;
	}

	/**
	 * @return the favorites
	 */
	public ArrayList<Favorite> getFavorites() {
		return favorites;
	}

	
	public void add(Favorite newFavorite) {
		favorites.add(newFavorite);
	}
	
	/**
	 * @param favorites the favorites to set
	 */
	public void setFavorites(ArrayList<Favorite> favorites) {
		this.favorites = favorites;
	}

	/**
	 * @return the unreadComments
	 */
	public java.util.ArrayList<UnreadMarker> getUnreadComments() {
		return unreadComments;
	}

	/**
	 * @param unreadComments the unreadComments to set
	 */
	public void setUnreadComments(java.util.ArrayList<UnreadMarker> unreadComments) {
		this.unreadComments = unreadComments;
	}

	/**
	 * @return the authorName
	 */
	public String getAuthorName() {
		return authorName;
	}

	/**
	 * @param authorName the authorName to set
	 */
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	/**
	 * @return the userName
	 */
	public String getUserName()
	{
	
		return userName;
	}
	
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName)
	{
	
		this.userName = userName;
	}	
}
