package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	
	private void replace(TagNode root, String oldTag, String newTag){
		if(root==null)
			return;
		if(root.tag.equals(oldTag))
			root.tag=newTag;
		
		replace(root.firstChild,oldTag,newTag);
		replace(root.sibling,oldTag,newTag);
			
	}
	private void replaceT(TagNode root, String oldTag, String newTag){
		if(root==null)
			return;

		if(root.tag.equals(oldTag))
			root.tag=newTag;
		
		replaceT(root.sibling,oldTag,newTag);
	}
	
	private void bold(TagNode root, int row){
		int count = 1;
		TagNode ptr = null;
		TagNode tmp=null;
		
		if(root==null)
			return;
		if(root.tag.equals("tr")){
			ptr=root;
		
			while(count!=row){
				ptr=ptr.sibling;
				count++;
			}
			ptr=ptr.firstChild;
			while(ptr!=null){
				tmp=ptr.firstChild;
					if(tmp.tag.equals("b")){
							continue;
					}else if(tmp.tag.equals("ol")||tmp.tag.equals("ul")||tmp.tag.equals("em")){
						TagNode t=ptr.firstChild.firstChild;
						tmp.firstChild=new TagNode("b",t,null);
					}else{
						ptr.firstChild= new TagNode("b",tmp,null);
					}
				ptr=ptr.sibling;
				}
			return;
			}
		bold(root.firstChild,row);
		bold(root.sibling,row);
		
	}
	
	private void add(TagNode root,TagNode bd, String word, String tag){
		boolean ch=false;
		boolean mult = false;
		TagNode ptr=null;
		
		if(root==null)
			return;
		
		if(root.firstChild==null){
			if(!bd.tag.equals(tag)){
			String[] str= root.tag.split(" ");
				for(int i=0;i<str.length;i++){
					ch=wordCheck(word,str[i]);
						if(ch==true){
							if(i==0){
								bd.firstChild= new TagNode(tag,null,null);
								bd.firstChild.firstChild=new TagNode(str[0],null,null);
								ptr=bd.firstChild;
							}else{
								ptr.sibling= new TagNode(tag,null,null);
								bd=ptr;
								ptr=ptr.sibling;
								ptr.firstChild=new TagNode(str[i],null,null);
							}
							mult=false;
						}else if(ch==false){
							if(i==0){
								bd.firstChild=new TagNode(str[i]+" ",null,null);
								ptr=bd.firstChild;
							}else{
								if(mult==true){
									ptr.tag=ptr.tag+str[i]+" ";
								}else{
									ptr.sibling=new TagNode(" " +str[i]+" ",null,null);
									bd=ptr;
									ptr=ptr.sibling;
								}
							}
							mult=true;
						}
						root=ptr;
				}
		}
	}
		//Behind Tag//
		if(bd.firstChild==root)
			bd=bd.firstChild;
		else if(bd.sibling==root)
			bd=bd.sibling;
		
		add(root.firstChild,bd,word,tag);
		add(root.sibling,bd,word,tag);
	}
	
	private boolean wordCheck(String search, String input){
		if(input.length()>search.length()+1||input.length()<search.length())
			return false;
		
			for(int j=0;j<search.length();j++){
				char ch=Character.toLowerCase(input.charAt(j));
				char s=Character.toLowerCase(search.charAt(j));
				if(ch==search.charAt(j)){
					continue;
				}else{
					return false;
					}
			}
			if(search.length()==input.length()){
				return true;
			}else{
				if(input.charAt(input.length()-1)=='.'||input.charAt(input.length()-1)==','||input.charAt(input.length()-1)=='?'||input.charAt(input.length()-1)=='!'||input.charAt(input.length()-1)==':'||input.charAt(input.length()-1)==';')
					return true;
				else
					return false;
			}
		}
	private void remove(TagNode root, TagNode bd, String tag){
		TagNode ptr=null;
		
		if(root==null)
			return;
		
		if(root.tag.equals(tag)){
			if(tag.equals("ol")||tag.equals("ul")){
				replaceT(root.firstChild,"li","p");
			}
			ptr=root.firstChild;
				while(ptr.sibling!=null){
					ptr=ptr.sibling;
				}
			ptr.sibling=root.sibling;
			
				if(bd.firstChild==root){
					bd.firstChild=root.firstChild;
					root=bd.firstChild;
				}else if(bd.sibling==root){
					bd.sibling=root.firstChild;
					root=bd.sibling;
				}
		}
		
		//Behind pointer//
		if(bd.firstChild==root)
			bd=bd.firstChild;
		else if(bd.sibling==root)
			bd=bd.sibling;
		
		//Traversal//
		remove(root.firstChild,bd,tag);
		remove(root.sibling,bd,tag);
	}
	
	/**
	 * Builds the DOM tree from input HTML file. The root of the 
	 * tree is stored in the root field.
	 */
	public void build() {
		Stack<TagNode> cl=new Stack<TagNode>();
		boolean tag;
		TagNode ptr;
		String sh;
		
		if(sc.hasNext()==false)
			return;
		
		String str= sc.nextLine();
		str=str.substring(1,str.length()-1);
		root = new TagNode(str,null,null);
		ptr=root;
		cl.push(ptr);
		tag=true;
		
			while(sc.hasNext()==true){
				str=sc.nextLine();
				
				//Closing tags//
				if(str.length()>1){
					if(str.charAt(1)=='/'){
						ptr=cl.pop();
						tag=false;
						continue;
						}
					}
					if(tag==true){
						ptr.firstChild= new TagNode(str,null,null);
						ptr=ptr.firstChild;
					}else if(tag==false){
						ptr.sibling= new TagNode(str,null,null);
						ptr=ptr.sibling;
					}
					
					//Determining Tags
					sh=ptr.tag;
					if(sh.charAt(0)=='<'){
						tag=true;
						ptr.tag=sh.substring(1,sh.length()-1);
						cl.push(ptr);
					}else{
						tag=false;
					}		
			}
	}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		replace(root,oldTag,newTag);
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		bold(root,row);
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		remove(root.firstChild,root,tag);
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		add(root.firstChild,root,word,tag);
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
}
