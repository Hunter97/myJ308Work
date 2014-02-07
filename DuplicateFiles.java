/*
 * Paul Wallace 
 * 90.308 Assignment 2
 */
package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;



/**
 *  Class that will recursively navigate a file directory and produce a list of 
 * directories and a list of files.  The Class will also find produce a list of
 * duplicate files found within the directory, from parent to sub-folder, and
 * produce a list of the same files.
 * 
 * @author prwallace
 */
public class DuplicateFiles {
    private List< File > m_dupFileList = new ArrayList<>();
    private List< File > m_fileList = new ArrayList<>();
    private List< File > m_dirList = new ArrayList<>();

    public DuplicateFiles() {}


    /**
     * Searches the folder, denoted by the Path argument, for the existence of
     * a single file. Returns true if a file a found if found.
     * 
     * @param path              - parent directory Path
     * @return haseFiles        - true when a file is found 
     * @throws IOException
     */
    public boolean hasFiles ( Path path ) throws IOException {
        boolean hasFiles = false;
        File file;

        if( path.toFile().isDirectory() && path.isAbsolute() ) {
            try ( DirectoryStream<Path> dirStream = Files.newDirectoryStream( path )) {
                for ( Path p : dirStream ) {
                    file = new File( p.toString() );

                    if( file.isFile() ) {
                        hasFiles = true;
                        break;
                    }  
                }
            }
        }

        return hasFiles;
    }


    /**
     * Creates a List of directories from the argument parent directory.  Also 
     * produces a separate list of files located within the parent directory and
     * its sub-directories.  The method produces recursive calls when a sub-directory
     * is found.
     * 
     * @param path              - parent directory Path   . 
     * @throws  IOException
     */
    public void createDirectoryFileLists( Path path ) throws IOException {
        try ( DirectoryStream< Path > dirStream = Files.newDirectoryStream( path ) ) {

            for ( Path p : dirStream ) {
                if( p.toFile().isDirectory() ) {
                //if(p.toFile().isFile() ) {
                    
                    createDirectoryFileLists( p );
                    m_dirList.add( p.toFile() );
                }
                else {
                    m_fileList.add( p.toFile() );
                }
            }
        }
    }


    /**
     *  Gets the file list produced by createDirectoryFileLists
     * 
     * @return              - returns a List< File > of files
     */
    public List< File > getFileList() {
        return m_fileList;
    }


    /**
     *  Gets the directory list produced by createDirectoryFileLists
     * 
     * @return              - returns a List< Files > of subdirectories
     */
    public List< File > getDirList() {
        return m_dirList;
    }


    /**
     *  Gets/returns a List< File > of duplicate files contained within a directory
     * system, beginning at its parent directory down to its lowest sub-directory.
     * 
     * @return                  - returns a List< File > of duplicate files.
     * @throws IOException
     */
    public List< File > getDuplicateFiles() throws IOException {
        int fromIndex = 1;
        int index = 0;
        boolean haveMatch = false;

        File fromTmpList;
        File fromFileList = m_fileList.get( 0 );
        ListIterator< File > fileItr = m_fileList.listIterator( fromIndex );
        List< File > tmpFileList = new ArrayList<>();
        tmpFileList.addAll( m_fileList.subList( fromIndex, m_fileList.size() - fromIndex ));

        while( fileItr.hasNext() && tmpFileList.size() > 0 ) {
            ListIterator< File > tmpFileIter = tmpFileList.listIterator( index );
            while( tmpFileIter.hasNext() ) {
                if(( fromTmpList = tmpFileIter.next() ).getName().equalsIgnoreCase( fromFileList.getName() )) {
                    if( this.areFilesEqual( fromFileList, fromTmpList )) {
                        haveMatch = true;
                    }
                }
            }

            if( haveMatch ) {
                m_dupFileList.add( fromFileList );
                haveMatch = false;
            }

            index++;
            fromFileList = fileItr.next();
        }

        return m_dupFileList;

    }   


    /**
     *  Tests attributes of 2 files to determine if they are the same file.  Tests
     *  the files size and last time modified.  If equal, the method will return true.
     * 
     * @param source            - File being tested against
     * @param target            - File to compare against source
     * @return                  - Returns true if both files are same file
     * @throws IOException
     */
    public boolean areFilesEqual( File source, File target ) throws IOException {
        boolean isSameFile = false;

        if( source.lastModified() == target.lastModified() && source.length() == target.length() ) {
            isSameFile = true;
        }

        System.out.format( "%s and &s are identical files, will be removing %s from file system\n", source, target, target );
        return isSameFile;
    }
}
