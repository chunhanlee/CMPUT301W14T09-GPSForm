package ca.cmput301w15t09.model.cache;

public class Cache {
	/* CACHE STRUCTURE:
	   USERS
	   USER
	 	USER PROFILE
	 		FAVORITES
			UNREAD MARKERS
	   CACHED ACTIONS
		ACTION TO POST */
	
	// WHAT ARE THE ACTIONS WE CAN POST?
	//  MAKE / EDIT A COMMENT - comment, action (MAKE/EDIT/FAVORITE/READ)
	//  FAVORITE A COMMENT - comment, new favorite = favorite name, comment
	//  READ A COMMENT - comment, new unreadMarker = commentStatus, comment
	// CORRELATIONS: ALL TYPES REQUIRE A BASE COMMENT AND AN ACTION.
	//  ACTION AND COMMENT DETERMINED AT POINT WHERE ACTION IS ADDED TO QUEUE
	//	Switch to handle changes in processing?

	
}
