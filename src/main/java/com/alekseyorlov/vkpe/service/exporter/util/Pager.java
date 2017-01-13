package com.alekseyorlov.vkpe.service.exporter.util;

import java.util.Iterator;

public class Pager implements Iterable<Pager.Page>{

    public static class Page {
        
        private int offset;
        
        private int count;

        public int getOffset() {
            return offset;
        }

        public int getCount() {
            return count;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public void setCount(int count) {
            this.count = count;
        }
        
    }
    
    private int count;
    
    private int pageSize;
    
    private int current;
    
    public Pager(int count, int pageSize) {
        this.count = count;
        this.pageSize = pageSize;
    }

    @Override
    public Iterator<Page> iterator() {
        
        return new Iterator<Page>() {

            @Override
            public boolean hasNext() {
                
                return current < count;
            }

            @Override
            public Page next() {
                Page page = new Page();
                
                page.setOffset(current);
                page.setCount(current + pageSize < count ? pageSize : count - current);
                
                current += pageSize;
                
                return page;
            }
            
        };
    }
 
}
